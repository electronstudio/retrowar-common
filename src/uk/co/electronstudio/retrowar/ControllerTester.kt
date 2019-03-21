package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Gdx

import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer.getAxis
import com.badlogic.gdx.utils.Align
import org.libsdl.SDL
import uk.co.electronstudio.retrowar.screens.GameSession

/**
 * Displays all connected controllers so we can test if the mappings are correct.
 */
class ControllerTester(session: GameSession) : SimpleGame(session, 640f, 480f, fadeInEffect = false) {

    override fun doLogic(deltaTime: Float) { // Called automatically every frame
    }

    override fun doDrawing(batch: Batch) { // called automatically every frame

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f) // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        font.draw(batch, "Controllers connected: ${App.app.controllers.getControllers().size}", 0f, 20f)
        //  batch.begin()

        var x = 0f
        for (i in 0..App.app.controllers.getControllers().size - 1) {
            var y = 472f
            val m = App.app.controllers.getControllers()[i]
            //  val c = m.controller
            font.draw(batch, m.name, x, y, 256f, Align.left, false)
            y -= 8
            // font.draw(batch, m.mapping, x, y, 256f, Align.left, false)
            for (j in 0..31) {
                if (m.getButton(j)) {
                    y -= 8f
                    val mapping: String = when (j) {
                        SDL.SDL_CONTROLLER_BUTTON_A -> "A"
                        SDL.SDL_CONTROLLER_BUTTON_B -> "B"
                        SDL.SDL_CONTROLLER_BUTTON_X -> "X"
                        SDL.SDL_CONTROLLER_BUTTON_Y -> "Y"
                        SDL.SDL_CONTROLLER_BUTTON_LEFTSHOULDER -> "L_BUMPER"
                        SDL.SDL_CONTROLLER_BUTTON_RIGHTSHOULDER -> "R_BUMPER"
                        SDL.SDL_CONTROLLER_BUTTON_GUIDE -> "GUIDE"
                        SDL.SDL_CONTROLLER_BUTTON_BACK -> "BACK"
                        SDL.SDL_CONTROLLER_BUTTON_START -> "START"
                        SDL.SDL_CONTROLLER_BUTTON_DPAD_DOWN -> "DPAD_DOWN"
                        SDL.SDL_CONTROLLER_BUTTON_DPAD_UP -> "DPAD_UP"
                        SDL.SDL_CONTROLLER_BUTTON_DPAD_LEFT -> "DPAD_LEFT"
                        SDL.SDL_CONTROLLER_BUTTON_DPAD_RIGHT -> "DPAD_RIGHT"
                        //     SDL.SDL_CONTROLLER_BUTTON_  -> "L_TRIGGER"
                        //    SDL.SDL_CONTROLLER_BUTTON_A  -> "R_TRIGGER"
                        SDL.SDL_CONTROLLER_BUTTON_RIGHTSTICK -> "R_STICK_PUSH"
                        SDL.SDL_CONTROLLER_BUTTON_LEFTSTICK -> "L_STICK_PUSH"
                        else -> "UNMAPPED BUTTON $j"
                    }
                    font.draw(batch, mapping, x, y, 256f, Align.left, false)
                }
            }

            for (j in 0..31) {
                if (m.getAxis(j) != 0f) {
                    y -= 8f
                    val mapping: String = when (j) {
                        SDL.SDL_CONTROLLER_AXIS_LEFTX -> "L_STICK_HORIZONTAL_AXIS"
                        SDL.SDL_CONTROLLER_AXIS_RIGHTX -> "R_STICK_HORIZONTAL_AXIS"
                        SDL.SDL_CONTROLLER_AXIS_LEFTY -> "L_STICK_VERTICAL_AXIS"
                        SDL.SDL_CONTROLLER_AXIS_RIGHTY -> "R_STICK_VERTICAL_AXIS"
                        SDL.SDL_CONTROLLER_AXIS_TRIGGERLEFT -> "L_TRIGGER_AXIS"
                        SDL.SDL_CONTROLLER_AXIS_TRIGGERRIGHT -> "R_TRIGGER_AXIS"
                        else -> "UNMAPPED AXIS $j"
                    }
                    font.draw(batch, "$mapping: ${m.getAxis(j)} ", x, y, 256f, Align.left, false)
                }
            }
            for (j in 0..31) {
                if (m.getPov(j) != PovDirection.center) {
                    y -= 8f
                    val mapping: String = when (j) {
                        // m.DPAD -> "DPAD"
                        else -> "UNKNOWN DPAD $j"
                    }
                    font.draw(batch, "$mapping ${m.getPov(j)}", x, y, 256f, Align.left, false)
                }
            }
            //            try {
            //                for (j in 0..31) {
            //                    y -= 8f
            //                    font.draw(batch, "Accel$j: ${c.getAccelerometer(j)}", x, y, 256f, Align.left, false)
            //                }
            //            } catch (e: GdxRuntimeException) {
            //            }
            //
            //            for (j in 0..31) {
            //                if (c.getSliderX(j)) {
            //                    y -= 8f
            //                    font.draw(batch, "XSlider$j: ${c.getSliderX(j)}", x, y, 256f, Align.left, false)
            //                }
            //            }
            //            for (j in 0..31) {
            //                if (c.getSliderY(j)) {
            //                    y -= 8f
            //                    font.draw(batch, "YSlider$j: ${c.getSliderY(j)}", x, y, 256f, Align.left, false)
            //                }
            //            }
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
