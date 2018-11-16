package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.GdxRuntimeException
import uk.co.electronstudio.retrowar.App.Companion.app
import uk.co.electronstudio.retrowar.screens.GameSession

/**
 * Displays all connected controllers so we can test if the mappings are correct.
 */
class ControllerTester(session: GameSession) : SimpleGame(session, 640f, 480f) {

    override fun doLogic(deltaTime: Float) { // Called automatically every frame
    }

    override fun doDrawing(batch: Batch) { // called automatically every frame

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f) // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        font.draw(batch, "Controllers connected: ${Controllers.getControllers().size}", 0f, 20f)
        //  batch.begin()

        var x = 0f
        for (i in 0..app.mappedControllers.lastIndex) {
            var y = 472f
            val m = app.mappedControllers[i]
            val c = m.controller
            font.draw(batch, c.name, x, y, 256f, Align.left, false)
            y -= 8
            font.draw(batch, m.mapping, x, y, 256f, Align.left, false)
            for (j in 0..31) {
                if (c.getButton(j)) {
                    y -= 8f
                    val mapping: String = when (j) {
                        m.A -> "A"
                        m.B -> "B"
                        m.X -> "X"
                        m.Y -> "Y"
                        m.L_BUMPER -> "L_BUMPER"
                        m.R_BUMPER -> "R_BUMPER"
                        m.GUIDE -> "GUIDE"
                        m.BACK -> "BACK"
                        m.START -> "START"
                        m.DPAD_DOWN -> "DPAD_DOWN"
                        m.DPAD_UP -> "DPAD_UP"
                        m.DPAD_LEFT -> "DPAD_LEFT"
                        m.DPAD_RIGHT -> "DPAD_RIGHT"
                        m.L_TRIGGER -> "L_TRIGGER"
                        m.R_TRIGGER -> "R_TRIGGER"
                        m.R_STICK_PUSH -> "R_STICK_PUSH"
                        m.L_STICK_PUSH -> "L_STICK_PUSH"
                        else -> "UNMAPPED BUTTON $j"
                    }
                    font.draw(batch, mapping, x, y, 256f, Align.left, false)
                }
            }

            for (j in 0..31) {
                if (c.getAxis(j) != 0f) {
                    y -= 8f
                    val mapping: String = when (j) {
                        m.L_STICK_HORIZONTAL_AXIS -> "L_STICK_HORIZONTAL_AXIS"
                        m.R_STICK_HORIZONTAL_AXIS -> "R_STICK_HORIZONTAL_AXIS"
                        m.L_STICK_VERTICAL_AXIS -> "L_STICK_VERTICAL_AXIS"
                        m.R_STICK_VERTICAL_AXIS -> "R_STICK_VERTICAL_AXIS"
                        m.L_TRIGGER_AXIS -> "L_TRIGGER_AXIS"
                        m.R_TRIGGER_AXIS -> "R_TRIGGER_AXIS"
                        else -> "UNMAPPED AXIS $j"
                    }
                    font.draw(batch, "$mapping: ${c.getAxis(j)} ", x, y, 256f, Align.left, false)
                }
            }
            for (j in 0..31) {
                if (c.getPov(j) != PovDirection.center) {
                    y -= 8f
                    val mapping: String = when (j) {
                        m.DPAD -> "DPAD"
                        else -> "UNKNOWN DPAD $j"
                    }
                    font.draw(batch, "$mapping ${c.getPov(j)}", x, y, 256f, Align.left, false)
                }
            }
            try {

                for (j in 0..31) {
                    y -= 8f
                    font.draw(batch, "Accel$j: ${c.getAccelerometer(j)}", x, y, 256f, Align.left, false)
                }
            } catch (e: GdxRuntimeException) {
            }

            for (j in 0..31) {
                if (c.getSliderX(j)) {
                    y -= 8f
                    font.draw(batch, "XSlider$j: ${c.getSliderX(j)}", x, y, 256f, Align.left, false)
                }
            }
            for (j in 0..31) {
                if (c.getSliderY(j)) {
                    y -= 8f
                    font.draw(batch, "YSlider$j: ${c.getSliderY(j)}", x, y, 256f, Align.left, false)
                }
            }
            x += 256f
        }

        //  batch.end()
    }

    // These methods must be implemented but don't have to do anything
    override fun show() {
    }

    override fun hide() {}

    override fun dispose() {}
}
