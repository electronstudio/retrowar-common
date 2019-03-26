package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controller

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
class ControllerTester(session: GameSession) : SimpleGame(session, 640f, 360f, fadeInEffect = false) {

    override fun doLogic(deltaTime: Float) { // Called automatically every frame
    }

    override fun doDrawing(batch: Batch) { // called automatically every frame

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f) // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        font.draw(batch, "Controllers connected: ${App.app.controllers.getControllers().size}", 0f, 20f)

        var y = height
        App.app.controllers.getControllers().forEachIndexed{index, controller->
            val x = if(index % 2 == 0) 0f else width/2f
            font.color=com.badlogic.gdx.graphics.Color.LIME
            font.draw(batch, controller.name.removePrefix("SDL GameController "), x, y, 256f, Align.left, false)
            drawController(controller, y, batch, x)
            y -=  if(index % 2 == 0) 0f else 50f
        }
    }

    private fun drawController(m: Controller, y: Float, batch: Batch, x: Float): Float {
        font.color=com.badlogic.gdx.graphics.Color.WHITE
        var y1 = y
        for (j in 0..31) {
            if (m.getButton(j)) {
                y1 -= 8f
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
                font.draw(batch, mapping, x, y1, 256f, Align.left, false)
            }
        }

        for (j in 0..31) {
            if (m.getAxis(j) != 0f) {
                y1 -= 8f
                val mapping: String = when (j) {
                    SDL.SDL_CONTROLLER_AXIS_LEFTX -> "L_STICK_HORIZONTAL_AXIS"
                    SDL.SDL_CONTROLLER_AXIS_RIGHTX -> "R_STICK_HORIZONTAL_AXIS"
                    SDL.SDL_CONTROLLER_AXIS_LEFTY -> "L_STICK_VERTICAL_AXIS"
                    SDL.SDL_CONTROLLER_AXIS_RIGHTY -> "R_STICK_VERTICAL_AXIS"
                    SDL.SDL_CONTROLLER_AXIS_TRIGGERLEFT -> "L_TRIGGER_AXIS"
                    SDL.SDL_CONTROLLER_AXIS_TRIGGERRIGHT -> "R_TRIGGER_AXIS"
                    else -> "UNMAPPED AXIS $j"
                }
                font.draw(batch, "$mapping: ${m.getAxis(j)} ", x, y1, 256f, Align.left, false)
            }
        }
        for (j in 0..31) {
            if (m.getPov(j) != PovDirection.center) {
                y1 -= 8f
                val mapping: String = when (j) {
                    // m.DPAD -> "DPAD"
                    else -> "UNKNOWN DPAD $j"
                }
                font.draw(batch, "$mapping ${m.getPov(j)}", x, y1, 256f, Align.left, false)
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
        return y1
    }

    // These methods must be implemented but don't have to do anything
    override fun show() {
    }

    override fun hide() {}

    override fun dispose() {}
}
