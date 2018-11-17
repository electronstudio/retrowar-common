package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Vector2
import uk.co.electronstudio.retrowar.App.Companion.app

/**
 * A framebuffer object.  Max size is the resolution of the display, but if you set it to smaller size
 * then only a smaller portion of the framebuffer will actually be used.  That way we can change
 * the virtual size of the framebuffer on the fly without allocating a new framebuffer object.
 */
class ManagedFBO {
    val MAX_WIDTH = Gdx.graphics.displayMode.width
    val MAX_HEIGHT = Gdx.graphics.displayMode.height
    private val fbo: FrameBuffer = FrameBuffer(Pixmap.Format.RGB888, MAX_WIDTH, MAX_HEIGHT, false)
    private val fboCam: OrthographicCamera = OrthographicCamera()

    internal val texture
        get() = fbo.colorBufferTexture

    var width: Float = 0f
    var height: Float = 0f

    val projectionMatrix
        get() = fboCam.combined

    fun resize(w: Float, h: Float, scale: Float) {
        width = Math.max(w / scale, 1f)
        height = Math.max(h / scale, 1f)

        val camWidth = MAX_WIDTH * scale // renderer.WIDTH
        val camHeight = MAX_HEIGHT * scale // renderer.HEIGHT
        fboCam.setToOrtho(false, camWidth, camHeight)

        fboCam.position.set(camWidth / 2f, camHeight / 2f, 0f)
        fboCam.update()
    }

    fun resizeToScreenSize(w: Float, h: Float) {
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()

        val camWidth = MAX_WIDTH.toFloat() / (width / w)
        val camHeight = MAX_HEIGHT.toFloat() / (height / h)

        fboCam.setToOrtho(false, camWidth, camHeight)
        // fboCam.position.set((camWidth / 2f).roundToInt().toFloat(), camHeight / 2f, 0f)
        fboCam.update()
    }

    fun begin() {
        fbo.begin()
    }

    fun end() {
        fbo.end()
    }

    fun updateShader(fboBatch: SpriteBatch) {
        val outVec = Vector2(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        val inVec = Vector2(width, height)
        val textureSize = Vector2(Gdx.graphics.displayMode.width.toFloat(), Gdx.graphics.displayMode.height.toFloat())
        app.shader.process(fboBatch, textureSize, inVec, outVec)
    }
}