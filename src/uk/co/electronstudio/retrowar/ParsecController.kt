package uk.co.electronstudio.retrowar

import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.math.Vector3
import com.parsecgaming.parsec.ParsecGamepadAxisMessage
import com.parsecgaming.parsec.ParsecGamepadButtonMessage
import com.parsecgaming.parsec.ParsecLibrary
import com.parsecgaming.parsec.ParsecLibrary.ParsecKeycode.*
import com.parsecgaming.parsec.ParsecMessage
import uk.co.electronstudio.parsec.InputEvent
import uk.co.electronstudio.retrowar.screens.GameSession
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

// FIXME this isn't really a controller, because it now includes keyboard input. so conceptually
// its akin to an InputDevice. it was easy to treat as an SDL controller because all the codes it
// returns happen to match the SDL codes but really we should move it up the class hierachy
// and use the Parsec codes.

class ParsecController(val id: Int, val guestName: String) : RumbleController {

    var player: Player? = null

    val buttonState = BooleanArray(15)
    val axisState = FloatArray(6)


    var x: Int = 0
    var y: Int = 0


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

    fun processMessage(event: InputEvent) {
        when (event) {
            is InputEvent.GamepadButtonEvent -> {
                log("button ${event.button} ${event.pressed}")
                if (event.button in 0..14) {
                    setButton(event.button, event.pressed)
                }
            }
            is InputEvent.GamepadAxisEvent -> {
                log("axis ${event.axis} ${event.value}")
                //if (event.value.toInt() != 0) {
                    setAxis(event.axis, event.value)
                //}
            }
            is InputEvent.KeyboardEvent -> {
                log("key ${event.code} ${event.pressed}")
                setKey(event.code, event.pressed)
            }
            is InputEvent.MouseButtonEvent -> {
                when (event.button) {
                    ParsecLibrary.ParsecMouseButton.MOUSE_L -> {
                        buttonState[SDL_CONTROLLER_BUTTON_A] = event.pressed
                        if(event.pressed){
                        val session = App.app.screen
                        val player = this.player
                        if (session is GameSession){
                            val game = session.game
                            if (game != null && game is Game.UsesMouseAsInputDevice && player != null) {
                                val vec =  game.getMouseFromGameCoordsFlipY(player, x.toFloat(), y.toFloat())
                                axisState[SDL_CONTROLLER_AXIS_RIGHTX] = vec.x
                                axisState[SDL_CONTROLLER_AXIS_RIGHTY] = vec.y
                            }
                        }}else{
                            axisState[SDL_CONTROLLER_AXIS_RIGHTX] = 0f
                            axisState[SDL_CONTROLLER_AXIS_RIGHTY] = 0f
                        }
                    }
                    ParsecLibrary.ParsecMouseButton.MOUSE_MIDDLE -> axisState[SDL_CONTROLLER_AXIS_TRIGGERLEFT] = if (event.pressed) 1f else 0f
                    ParsecLibrary.ParsecMouseButton.MOUSE_R -> axisState[SDL_CONTROLLER_AXIS_TRIGGERRIGHT] = if (event.pressed) 1f else 0f
                }
            }

            is InputEvent.MouseMotionEvent -> {
                //log("mouse motion ${event.relative} ${event.x} ${event.y}")
                x=event.x
                y=event.y
            }
        }

    }

    private fun setKey(code: Int, pressed: Boolean) {
        when (code) {
            KEY_LSHIFT, KEY_Z ->
                axisState[SDL_CONTROLLER_AXIS_TRIGGERLEFT] = if (pressed) 1f else 0f

            KEY_RSHIFT, KEY_SLASH ->
                axisState[SDL_CONTROLLER_AXIS_TRIGGERRIGHT] = if (pressed) 1f else 0f

            KEY_UP -> axisState[SDL_CONTROLLER_AXIS_RIGHTY] = if (pressed) -1f else 0f
            KEY_DOWN -> axisState[SDL_CONTROLLER_AXIS_RIGHTY] = if (pressed) 1f else 0f
            KEY_LEFT -> axisState[SDL_CONTROLLER_AXIS_RIGHTX] = if (pressed) -1f else 0f
            KEY_RIGHT -> axisState[SDL_CONTROLLER_AXIS_RIGHTX] = if (pressed) 1f else 0f
            else -> {
                val gamepadButton = when (code) {
                    KEY_W, KEY_KP_8 -> SDL_CONTROLLER_BUTTON_DPAD_UP
                    KEY_S, KEY_KP_2 -> SDL_CONTROLLER_BUTTON_DPAD_DOWN
                    KEY_A, KEY_KP_4 -> SDL_CONTROLLER_BUTTON_DPAD_LEFT
                    KEY_D, KEY_KP_6 -> SDL_CONTROLLER_BUTTON_DPAD_RIGHT
                    KEY_SPACE -> SDL_CONTROLLER_BUTTON_A
                    KEY_ENTER -> SDL_CONTROLLER_BUTTON_B
                    KEY_LCTRL -> SDL_CONTROLLER_BUTTON_X
                    KEY_RCTRL -> SDL_CONTROLLER_BUTTON_Y
                    KEY_TAB -> SDL_CONTROLLER_BUTTON_LEFTSHOULDER
                    KEY_E -> SDL_CONTROLLER_BUTTON_RIGHTSHOULDER
                    else -> -1
                }
                if(gamepadButton != -1) {
                    buttonState[gamepadButton] = pressed
                }else{
                    log("parsec key input $code")
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
