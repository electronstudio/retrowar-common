package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import uk.co.electronstudio.retrowar.Prefs.BinPref.FPS

/**
 * Renders sprites to a FrameBufferObject and thence to the screen
 * Does not support bilinear filtering when smooth motion is enabled
 * Creates new objects every frame, not sure how heavy they are or if they could better be pooled and reused
 */
class FBORenderer(val WIDTH: Float, val HEIGHT: Float, val fadeInEffect: Boolean) {

    var cam: OrthographicCamera = setupCam(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    internal var shape = ShapeRenderer(5000, createDefaultShapeShader())
    internal var batch = SpriteBatch(1000, createDefaultShader())

    internal var scaleFactor = 1f

    var timer = 0.0f

    internal var fboBatch = SpriteBatch(100, createDefaultShader())
    internal var glyphLayout = GlyphLayout()

    private val mFBO = ManagedFBO()

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

        drawScanlines(shape, cam)
    }

    fun beginFBO(): Batch {
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

    fun getShape(): ShapeRenderer {
        shape.projectionMatrix = mFBO.projectionMatrix
        return shape
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

    var scaledWidth = 0f
    var scaledHeight = 0f
    var m = 0f

    fun setupCam(x: Float, h: Float): OrthographicCamera {
        val w = x
        // val m: Float
        m = findAppropriateScaleFactor(w, h)
        scaledWidth = WIDTH * m
        scaledHeight = HEIGHT * m
        log("setupcam $scaledWidth $scaledHeight $m")
        val cam = OrthographicCamera((w) / m, h / m)
        cam.translate((WIDTH / 2), (HEIGHT / 2))
        cam.update()
        return cam
    }

    fun findAppropriateScaleFactor(w: Float, h: Float): Float =
        if (Prefs.BinPref.STRETCH.isEnabled()) findHighestScaleFactor(w, h)
        else findHighestIntegerScaleFactor(w, h)

    fun findHighestIntegerScaleFactor(width: Float, height: Float): Float {
        val w = width / WIDTH
        val h = height / HEIGHT
        return if (w < h) w.roundDown() else h.roundDown()
    }

    fun findHighestScaleFactor(width: Float, height: Float): Float {
        val w = width / WIDTH
        val h = height / HEIGHT
        return if (w < h) w else h
    }

    fun drawScanlines(shape: ShapeRenderer, cam: Camera) {
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

    fun convertGameCoordsToScreenCoords(x: Float, y: Float): Vector3 {
        return cam.project(Vector3(x, y, 0f))
    }

    fun convertScreenToGameCoords(x: Int, y: Int): Vector3 {
        val g = cam.unproject(Vector3(x.toFloat(), y.toFloat(), 0f))
        //  log("convertcoords","${g.x} ${g.y}")
        // g.y = Renderer.HEIGHT-g.y
        return g
    }
}
