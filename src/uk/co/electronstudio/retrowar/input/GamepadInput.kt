package uk.co.electronstudio.retrowar.input

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.PovDirection
import org.libsdl.SDL
import uk.co.electronstudio.retrowar.utils.Vec

/**
 * Created by Richard on 13/08/2016.
 * Maps a controller to an input
 * Now assumes all controllers are SDL mappings
 */
internal class GamepadInput(val controller: Controller) : InputDevice() {

    var singleStickMode = true
    var lockedFireDirection: Vec? = null

    override val leftStick: Vec
        get() {

            val analog = filterDeadzone(0.05f,
                controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTX),
                controller.getAxis(SDL.SDL_CONTROLLER_AXIS_LEFTY))
            if(analog.isMoreOrLessZero()){
                return dpadAsStick()
            }else{
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
        val x = if(controller.getButton(SDL.SDL_CONTROLLER_BUTTON_DPAD_LEFT)) -1f
        else if(controller.getButton(SDL.SDL_CONTROLLER_BUTTON_DPAD_RIGHT)) 1f
        else 0f
        val y = if(controller.getButton(SDL.SDL_CONTROLLER_BUTTON_DPAD_UP)) -1f
        else if(controller.getButton(SDL.SDL_CONTROLLER_BUTTON_DPAD_DOWN)) 1f
        else 0f
        return Vec(x,y)
    }

    override val rightStick: Vec
        get() {
            // if(controller.RStickHorizontalAxis()!=0f || controller.RStickVerticalAxis()!=0f) singleStickMode=false

            val rawData = filterDeadzone(0.6f, controller.getAxis(SDL.SDL_CONTROLLER_AXIS_RIGHTX), controller.getAxis(SDL.SDL_CONTROLLER_AXIS_RIGHTY))
            if (rawData.isMoreOrLessZero()) {
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
                return rawData
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
