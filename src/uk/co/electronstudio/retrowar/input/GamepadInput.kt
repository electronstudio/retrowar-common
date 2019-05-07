package uk.co.electronstudio.retrowar.input

import org.libsdl.SDL
import uk.co.electronstudio.retrowar.Prefs
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

    var lastDirectionPushedOnLeftStick = Vec(0f, 0f)

    override fun rumble(left: Float, right: Float, duration_ms: Int) {
        val scale = when(Prefs.MultiChoicePref.RUMBLE.getNum()){
            0 -> 1f
            1 -> 0.5f
            else -> 0f
        }
        controller.rumble(left*scale, right*scale, duration_ms)
    }

    override val movementVec: Vec
        get() {

            val analog = Vec(controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTX),
                controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTY)).ignoreDeadzone(Prefs.NumPref.DEADZONE.asPercent())
                .clampMagnitude(1.0f)

            val dpad = dpadAsStick()

            if(dpad.isNotZero()){
                lastDirectionPushedOnLeftStick = dpad
                return dpad
            }else if(analog.isNotZero()){
                lastDirectionPushedOnLeftStick = analog
                return analog
            }else{
                return Vec(0f,0f)
            }

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

    override val aimingVec: Vec
        get() {
            // if(controller.RStickHorizontalAxis()!=0f || controller.RStickVerticalAxis()!=0f) singleStickMode=false

            val analog = Vec(controller.getAxis(SDL.SDL_CONTROLLER_AXIS_RIGHTX),
                controller.getAxis(SDL.SDL_CONTROLLER_AXIS_RIGHTY)).ignoreDeadzone(Prefs.NumPref.DEADZONE.asPercent())
                .clampMagnitude(1.0f)
            if (analog.isMoreOrLessZero()) { // fixme this is unigame specific hack, should be moved there
                if (rightBumper || A || B || X || Y) {
                    if (lockedFireDirection == null) {
                        lockedFireDirection = lastDirectionPushedOnLeftStick.normVector()
                    }
                } else {
                    lockedFireDirection = null
                }
                return lockedFireDirection ?: Vec(0f,0f)
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
