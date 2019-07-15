package uk.co.electronstudio.retrowar

import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.math.Vector3
import com.parsecgaming.parsec.ParsecGamepadAxisMessage
import com.parsecgaming.parsec.ParsecGamepadButtonMessage
import com.parsecgaming.parsec.ParsecLibrary
import com.parsecgaming.parsec.ParsecMessage
import uk.co.electronstudio.sdl2gdx.RumbleController


val SDL_CONTROLLER_AXIS_INVALID = -1
val SDL_CONTROLLER_AXIS_LEFTX = 0
val SDL_CONTROLLER_AXIS_LEFTY = 1
val SDL_CONTROLLER_AXIS_RIGHTX = 2
val SDL_CONTROLLER_AXIS_RIGHTY = 3
val SDL_CONTROLLER_AXIS_TRIGGERLEFT = 4
val SDL_CONTROLLER_AXIS_TRIGGERRIGHT = 5
val SDL_CONTROLLER_AXIS_MAX = 6

/**
 * The list of buttons available from a controller
 */

val SDL_CONTROLLER_BUTTON_INVALID = -1
val SDL_CONTROLLER_BUTTON_A = 0
val SDL_CONTROLLER_BUTTON_B = 1
val SDL_CONTROLLER_BUTTON_X = 2
val SDL_CONTROLLER_BUTTON_Y = 3
val SDL_CONTROLLER_BUTTON_BACK = 4
val SDL_CONTROLLER_BUTTON_GUIDE = 5
val SDL_CONTROLLER_BUTTON_START = 6
val SDL_CONTROLLER_BUTTON_LEFTSTICK = 7
val SDL_CONTROLLER_BUTTON_RIGHTSTICK = 8
val SDL_CONTROLLER_BUTTON_LEFTSHOULDER = 9
val SDL_CONTROLLER_BUTTON_RIGHTSHOULDER = 10
val SDL_CONTROLLER_BUTTON_DPAD_UP = 11
val SDL_CONTROLLER_BUTTON_DPAD_DOWN = 12
val SDL_CONTROLLER_BUTTON_DPAD_LEFT = 13
val SDL_CONTROLLER_BUTTON_DPAD_RIGHT = 14
val SDL_CONTROLLER_BUTTON_MAX = 15


val SDL_HAT_CENTERED = 0x00
val SDL_HAT_UP = 0x01
val SDL_HAT_RIGHT = 0x02
val SDL_HAT_DOWN = 0x04
val SDL_HAT_LEFT = 0x08
val SDL_HAT_RIGHTUP = SDL_HAT_RIGHT or SDL_HAT_UP
val SDL_HAT_RIGHTDOWN = SDL_HAT_RIGHT or SDL_HAT_DOWN
val SDL_HAT_LEFTUP = SDL_HAT_LEFT or SDL_HAT_UP
val SDL_HAT_LEFTDOWN = SDL_HAT_LEFT or SDL_HAT_DOWN

val zero = Vector3(0f, 0f, 0f)

class ParsecController(val id: Int, val guestName: String) : RumbleController {


    val buttonState = BooleanArray(15)
    val axisState = FloatArray(6)

    override fun rumble(leftMagnitude: Float, rightMagnitude: Float, duration_ms: Int): Boolean {
        return false
    }

    override fun getAxis(axisCode: Int): Float {
        return axisState[axisCode]
    }

    override fun getName(): String {
        return "ParsecController $guestName"
    }

    override fun addListener(listener: ControllerListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeListener(listener: ControllerListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun getAccelerometer(accelerometerCode: Int): Vector3 {
        return zero
    }


    override fun setAccelerometerSensitivity(sensitivity: Float) {
    }

    override fun getButton(buttonCode: Int): Boolean {
        return buttonState[buttonCode]
    }

    override fun getPov(povCode: Int): PovDirection {
        return when {
            buttonState[SDL_CONTROLLER_BUTTON_DPAD_UP] && buttonState[SDL_CONTROLLER_BUTTON_DPAD_RIGHT] -> PovDirection.northEast
            buttonState[SDL_CONTROLLER_BUTTON_DPAD_UP] && buttonState[SDL_CONTROLLER_BUTTON_DPAD_LEFT] -> PovDirection.northWest
            buttonState[SDL_CONTROLLER_BUTTON_DPAD_DOWN] && buttonState[SDL_CONTROLLER_BUTTON_DPAD_RIGHT] -> PovDirection.southEast
            buttonState[SDL_CONTROLLER_BUTTON_DPAD_DOWN] && buttonState[SDL_CONTROLLER_BUTTON_DPAD_LEFT] -> PovDirection.southWest
            buttonState[SDL_CONTROLLER_BUTTON_DPAD_UP] -> PovDirection.north
            buttonState[SDL_CONTROLLER_BUTTON_DPAD_RIGHT] -> PovDirection.east
            buttonState[SDL_CONTROLLER_BUTTON_DPAD_DOWN] -> PovDirection.south
            buttonState[SDL_CONTROLLER_BUTTON_DPAD_LEFT] -> PovDirection.west
            else -> PovDirection.center
        }
    }

    override fun getSliderY(sliderCode: Int): Boolean {
        return false
    }

    override fun getSliderX(sliderCode: Int): Boolean {
        return false
    }

    fun processMessage(event: ParsecWrapper.InputEvent) {
        when (event) {
            is ParsecWrapper.InputEvent.GamepadButtonEvent -> {
                if (event.button in 1..14) {
                    setButton(event.button, event.pressed)
                }
            }
            is ParsecWrapper.InputEvent.GamepadAxisEvent -> {
                if (event.value.toInt() != 0) {
                    setAxis(event.axis, event.value)
                }
            }
        }

    }


    private fun setAxis(axis: Int, value: Short) {
        if (axis < axisState.size) axisState[axis] = value.toFloat() / Short.MAX_VALUE.toFloat()
        else error("Parsec controller axis out of bounds $axis")
    }

    private fun setButton(button: Int, pressed: Boolean) {
        if (button < buttonState.size) buttonState[button] = pressed
        else error("Parsec controller button out of bounds $button")
    }

}
