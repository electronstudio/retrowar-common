package uk.co.electronstudio.retrowar.input

import com.badlogic.gdx.controllers.Controller
import org.libsdl.SDL
import uk.co.electronstudio.retrowar.Prefs
import uk.co.electronstudio.retrowar.log
import uk.co.electronstudio.retrowar.utils.Vec
import uk.co.electronstudio.sdl2gdx.SDL2Controller

/**
 * Created by Richard on 13/08/2016.
 * Maps a controller to an input
 * Now assumes all controllers are SDL mappings
 */
internal class GamepadInput(val controller: SDL2Controller) : InputDevice() {

 //   var singleStickMode = true
    var lockedFireDirection: Vec? = null


    override fun rumble(left: Float, right: Float, duration_ms: Int) {
        controller.rumble(left, right, duration_ms)
    }


    override val leftStick: Vec
        get() {

            val analog = Vec(
                controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTX),
                controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTY)
            ).ignoreDeadzone(Prefs.NumPref.DEADZONE.asPercent()).clampMagnitude(1.0f)

            if (analog.isMoreOrLessZero()) {
                return dpadAsStick()
            } else {
                return analog
            }

//            return when(controller.getPov(0)){ // TODO untested, not sure any SDL controllers even have pov?
//                PovDirection.north -> Vec(0f, -1f)
//                PovDirection.northEast -> Vec(1f, -1f)
//                PovDirection.northWest -> Vec(-1f, -1f)
//                PovDirection.south -> Vec(0f, 1f)
//                PovDirection.southEast -> Vec(1f, 1f)
//                PovDirection.southWest -> Vec(-1f, 1f)
//                PovDirection.east -> Vec(1f, 0f)
//                PovDirection.west -> Vec(-1f, 0f)
//                else -> filterDeadzone(0.05f,
//                    controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTX),
//                    controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTY))
//            }
        }

    private fun dpadAsStick(): Vec {
        val x = if (controller.getButton(SDL.SDL_CONTROLLER_BUTTON_DPAD_LEFT)) -1f
        else if (controller.getButton(SDL.SDL_CONTROLLER_BUTTON_DPAD_RIGHT)) 1f
        else 0f
        val y = if (controller.getButton(SDL.SDL_CONTROLLER_BUTTON_DPAD_UP)) -1f
        else if (controller.getButton(SDL.SDL_CONTROLLER_BUTTON_DPAD_DOWN)) 1f
        else 0f
        return Vec(x, y).clampMagnitude(1.0f)
    }

    override val rightStick: Vec
        get() {
            // if(controller.RStickHorizontalAxis()!=0f || controller.RStickVerticalAxis()!=0f) singleStickMode=false

            val analog = Vec(
                controller.getAxis(SDL.SDL_CONTROLLER_AXIS_RIGHTX),
                controller.getAxis(SDL.SDL_CONTROLLER_AXIS_RIGHTY)
                ).ignoreDeadzone(Prefs.NumPref.DEADZONE.asPercent()).clampMagnitude(1.0f)
            if (analog.isMoreOrLessZero()) {    //fixme this is unigame specific hack, should be moved there
                if (rightBumper || A) {
                    if (lockedFireDirection == null) {
                        lockedFireDirection = leftStick
                    }
                    return lockedFireDirection!!
                } else {
                    lockedFireDirection = null
                    return Vec(0f, 0f)
                }
            } else {
                return analog
            }
        }

    override val leftTrigger: Float
        get() {
            return controller.getAxis(SDL.SDL_CONTROLLER_AXIS_TRIGGERLEFT)
        }
    override val rightTrigger: Float
        get() {
            return controller.getAxis(SDL.SDL_CONTROLLER_AXIS_TRIGGERRIGHT)
        }

    override val A: Boolean
        get() {
            return controller.getButton(SDL.SDL_CONTROLLER_BUTTON_A)
        }

    override val B: Boolean
        get() {
            return controller.getButton(SDL.SDL_CONTROLLER_BUTTON_B)
        }

    override val X: Boolean
        get() {
            return controller.getButton(SDL.SDL_CONTROLLER_BUTTON_X)
        }

    override val Y: Boolean
        get() {
            return controller.getButton(SDL.SDL_CONTROLLER_BUTTON_Y)
        }

    override val leftBumper: Boolean
        get() {
            return controller.getButton(SDL.SDL_CONTROLLER_BUTTON_LEFTSHOULDER)
        }

    override val rightBumper: Boolean
        get() {
            return controller.getButton(SDL.SDL_CONTROLLER_BUTTON_RIGHTSHOULDER)
        }
}
