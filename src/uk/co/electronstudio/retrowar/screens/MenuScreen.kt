package uk.co.electronstudio.retrowar.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector3
import uk.co.electronstudio.retrowar.App.Companion.app
import uk.co.electronstudio.retrowar.FBORenderer
import uk.co.electronstudio.retrowar.log
import uk.co.electronstudio.retrowar.menu.MenuController

abstract class MenuScreen(val drawBox: Boolean) : ScreenAdapter() {
    val WIDTH = 416f
    val HEIGHT = 256f

    abstract val controller: MenuController

    val renderer = FBORenderer(WIDTH, HEIGHT, false)

    override fun render(delta: Float) {
        val mouse = renderer.convertScreenToGameCoords(Gdx.input.x, Gdx.input.y)

        controller.doMouseInput(mouse.x, mouse.y)
        controller.doInput()

        renderToFBO(renderer.beginFBO())
        renderer.renderFBOtoScreen()
    }

    fun renderToFBO(batch: Batch) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f) // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        if (drawBox) controller.draw(batch)
        else controller.drawFlashing(batch)
        additionalRendering(batch)
        batch.end()
    }

    abstract fun additionalRendering(batch: Batch)

    override fun show() {
        app.clearEvents()
    }

    override fun resize(width: Int, height: Int) {
        log("resize $width $height")
        renderer.resize(width, height)
    }
}
