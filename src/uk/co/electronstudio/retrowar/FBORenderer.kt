package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.parsecgaming.parsec.ParsecLibrary
import uk.co.electronstudio.retrowar.Prefs.BinPref.FPS

/**
 * Renders sprites to a FrameBufferObject and thence to the screen
 * Does not support bilinear filtering when smooth motion is enabled
 */
class FBORenderer(val WIDTH: Float, val HEIGHT: Float, val fadeInEffect: Boolean) {

    private var cam: OrthographicCamera = setupCam(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    private val shape = ShapeRenderer(5000, createDefaultShapeShader())
    private val batch = SpriteBatch(8191, createDefaultShader())
    private val mFBO = ManagedFBO()

    private var fboBatch = SpriteBatch(1000, createDefaultShader())
    private var glyphLayout = GlyphLayout()

    private var scaleFactor = 1f
    private var timer = 0.0f
    private var scaledWidth = 0f
    private var scaledHeight = 0f
    private var m = 0f

    private val parsecBuffer: FrameBuffer = FrameBuffer(Pixmap.Format.RGB888, WIDTH.toInt(), HEIGHT.toInt(), false)
    private val parsecCam: OrthographicCamera = OrthographicCamera(WIDTH, HEIGHT)
    private val parsecBatch = SpriteBatch(1000, createDefaultShader())

    fun dispose(){
        shape.dispose()
        batch.dispose()
        mFBO.dispose()
        fboBatch.dispose()
    }

    fun renderFBOtoScreen() {
        endFBO()

        cam.update()
        fboBatch.projectionMatrix = cam.combined

        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        Prefs.BinPref.BILINEAR.filter(mFBO.texture)

        fboBatch.begin()

        mFBO.updateShader(fboBatch)

        fboBatch.draw(mFBO.texture, 0f, 0f, 0f, 0f,
            //      mFBO.width, mFBO.height,
            WIDTH, HEIGHT, 1f, 1f, 0f, 0, 0, mFBO.width.toInt(), mFBO.height.toInt(),
            //  WIDTH.toInt(), HEIGHT.toInt(),
            false, true)

        fboBatch.end()


        parsecCam.update()
        parsecBatch.projectionMatrix = parsecCam.combined
        parsecBuffer.begin()
        parsecBatch.begin()
        parsecBatch.draw(mFBO.texture, -WIDTH/2f, -HEIGHT/2f, 0f, 0f,
                //      mFBO.width, mFBO.height,
                WIDTH, HEIGHT, 1f, 1f, 0f, 0, 0, mFBO.width.toInt(), mFBO.height.toInt(),
                //  WIDTH.toInt(), HEIGHT.toInt(),
                false, true)
        parsecBatch.end()
        parsecBuffer.end()


        App.app.parsec?.apply {
            if(state==Parsec.State.HOSTING_GAME) {
                submitFrame(parsecBuffer.colorBufferTexture)
            }
        }

        App.app.parsec?.pollInput()


        drawScanlines(shape, cam)
    }


    fun beginFBO(): SpriteBatch {
        timer += Gdx.graphics.deltaTime

        if (fadeInEffect && timer < 3f) {
            scaleFactor = 4f * (-MathUtils.log2(timer) + 1.5f)
            if (scaleFactor < 1f) scaleFactor = 1f
        } else {
            scaleFactor = 1f
        }

        if (Prefs.BinPref.SMOOTH.isEnabled()) {
            mFBO.resizeToScreenSize(WIDTH, HEIGHT)
        } else {
            mFBO.resize(WIDTH, HEIGHT, scaleFactor)
        }
        mFBO.begin()

        batch.projectionMatrix = mFBO.projectionMatrix

        return batch
    }

//    fun getShape(): ShapeRenderer {
//        shape.projectionMatrix = mFBO.projectionMatrix
//        return shape
//    }

    fun resize(width: Int, height: Int) {
        log("FBOrenderer resize")
        // mFBO.resizeToScreenSize(WIDTH, HEIGHT, scaleFactor, m)

        cam = setupCam(width.toFloat(), height.toFloat())
    }

    // fixme messes up batch somehow
    fun darkenScreen(c: Color) {
        // batch.end()

        shape.projectionMatrix = mFBO.projectionMatrix

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shape.begin(Filled)
        shape.color = c

        shape.rect(0f, 0f, WIDTH, HEIGHT)

        shape.end()

        Gdx.gl.glDisable(GL20.GL_BLEND)

        //     batch.begin()
    }

    fun convertGameCoordsToScreenCoords(x: Float, y: Float): Vector3 {
        return cam.project(Vector3(x, y, 0f))
    }

    fun convertScreenToGameCoords(x: Int, y: Int): Vector3 {
        val g = cam.unproject(Vector3(x.toFloat(), y.toFloat(), 0f))
        //  log("convertcoords","${g.x} ${g.y}")
        // g.y = Renderer.HEIGHT-g.y
        return g
    }

    private fun endFBO() {
        batch.begin()
        if (FPS.isEnabled()) {
            Resources.FONT_ENGLISH.setColor(Color.WHITE)
            glyphLayout.setText(Resources.FONT_ENGLISH, "FPS ${Gdx.graphics.framesPerSecond}")
            Resources.FONT_ENGLISH.draw(batch, glyphLayout, WIDTH / 2 - glyphLayout.width / 2, HEIGHT + 1)
        }

        batch.end()

        mFBO.end()
    }

    private fun setupCam(x: Float, h: Float): OrthographicCamera {
        m = findAppropriateScaleFactor(x, h)
        scaledWidth = WIDTH * m
        scaledHeight = HEIGHT * m
        log("setupcam $scaledWidth $scaledHeight $m")
        val cam = OrthographicCamera((x) / m, h / m)
        cam.translate((WIDTH / 2), (HEIGHT / 2))
        cam.update()
        return cam
    }

    private fun findAppropriateScaleFactor(w: Float, h: Float): Float =
        if (Prefs.BinPref.STRETCH.isEnabled()) findHighestScaleFactor(w, h)
        else findHighestIntegerScaleFactor(w, h)

    private fun findHighestIntegerScaleFactor(width: Float, height: Float): Float {
        val w = width / WIDTH
        val h = height / HEIGHT
        return if (w < h) w.roundDown() else h.roundDown()
    }

    private fun findHighestScaleFactor(width: Float, height: Float): Float {
        val w = width / WIDTH
        val h = height / HEIGHT
        return if (w < h) w else h
    }

    private fun drawScanlines(shape: ShapeRenderer, cam: Camera) {
        if (Prefs.BinPref.SCANLINES.isEnabled()) {
            shape.projectionMatrix = cam.combined

            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

            shape.begin(ShapeRenderer.ShapeType.Line)
            shape.setColor(0.0f, 0.0f, 0.0f, 0.5f)

            for (i in 0..HEIGHT.toInt()) {
                val y = i.toFloat()
                shape.line(0f, y, WIDTH, y)
            }
            shape.end()

            Gdx.gl.glDisable(GL20.GL_BLEND)
        }
    }
}
