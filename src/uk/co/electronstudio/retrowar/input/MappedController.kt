package uk.co.electronstudio.retrowar.input

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.controllers.PovDirection

import uk.co.electronstudio.retrowar.isLinux
import uk.co.electronstudio.retrowar.isOSX

/**
 * Wraps a GDX controller, provides mapping for buttons/axises because GDX seems to lack this
 */
internal class MappedController(val controller: Controller) {

    var A: Int = 1
    var B: Int = 2
    var X: Int = 0
    var Y: Int = 3
    var GUIDE: Int = 12
    var L_BUMPER: Int = 4
    var R_BUMPER: Int = 5
    var BACK: Int = 8
    var START: Int = 9
    var DPAD_UP: Int = -1
    var DPAD_DOWN: Int = -1
    var DPAD_LEFT: Int = -1
    var DPAD_RIGHT: Int = -1
    var L_STICK_PUSH: Int = 10
    var R_STICK_PUSH: Int = 11

    var DPAD = 0
    var combinedDpad = true

    // Axes

    var L_TRIGGER: Int = 6
    var R_TRIGGER: Int = 7
    var L_TRIGGER_AXIS: Int = -1
    var R_TRIGGER_AXIS: Int = -1

    /** left stick vertical axis, -1 if up, 1 if down  */
    var L_STICK_VERTICAL_AXIS: Int = 2
    /** left stick horizontal axis, -1 if left, 1 if right  */
    var L_STICK_HORIZONTAL_AXIS: Int = 3
    /** right stick vertical axis, -1 if up, 1 if down  */
    var R_STICK_VERTICAL_AXIS: Int = 0
    /** right stick horizontal axis, -1 if left, 1 if right  */
    var R_STICK_HORIZONTAL_AXIS: Int = 1

    var mapping = "Unknown"

    var crippledTrigger = false

    fun setToSDLDefault() {
        mapping = "SDL"
        A = 0
        B = 1
        X = 2
        Y = 3
        START = 4
        GUIDE = 5
        BACK = 6
        L_STICK_PUSH = 7
        R_STICK_PUSH = 8
        L_BUMPER = 9
        R_BUMPER = 10
        DPAD_UP = 11
        DPAD_DOWN = 12
        DPAD_LEFT = 13
        DPAD_RIGHT = 14

        R_TRIGGER = -1
        L_TRIGGER = -1



        L_TRIGGER_AXIS = 4
        R_TRIGGER_AXIS = 5
        crippledTrigger = false



        DPAD = 0

        L_STICK_VERTICAL_AXIS = 1
        L_STICK_HORIZONTAL_AXIS = 0
        R_STICK_VERTICAL_AXIS = 3
        R_STICK_HORIZONTAL_AXIS = 2
    }

    fun setToMacDefault() {
        mapping = "MAC Default"
        A = 1
        B = 2
        X = 0
        Y = 3
        GUIDE = 12
        L_BUMPER = 4
        R_BUMPER = 5
        BACK = 8
        START = 9
        DPAD_UP = -1
        DPAD_DOWN = -1
        DPAD_LEFT = -1
        DPAD_RIGHT = -1
        L_STICK_PUSH = 10
        R_STICK_PUSH = 11

        DPAD = -1

        L_TRIGGER_AXIS = 4
        R_TRIGGER_AXIS = 5

        R_TRIGGER = 7
        L_TRIGGER = 6

        L_STICK_VERTICAL_AXIS = 1

        L_STICK_HORIZONTAL_AXIS = 0

        R_STICK_VERTICAL_AXIS = 3

        R_STICK_HORIZONTAL_AXIS = 2
    }

    fun setToLinuxDefault() {
        mapping = "Linux Default"
        A = 0
        B = 1
        X = 3
        Y = 2
        GUIDE = 10 // was 12
        L_BUMPER = 4
        R_BUMPER = 5
        BACK = 8
        START = 9
        DPAD_UP = -1
        DPAD_DOWN = -1
        DPAD_LEFT = -1
        DPAD_RIGHT = -1
        L_STICK_PUSH = 11 // was 10
        R_STICK_PUSH = 12 // was 11
        DPAD = 0
        L_TRIGGER_AXIS = 2
        R_TRIGGER_AXIS = 5
        R_TRIGGER = 7
        L_TRIGGER = 6
        L_STICK_VERTICAL_AXIS = 1
        L_STICK_HORIZONTAL_AXIS = 0
        R_STICK_VERTICAL_AXIS = 4
        R_STICK_HORIZONTAL_AXIS = 3
    }

    fun setToLinuxXbox() {
        mapping = "Linux Xbox"
        A = 0
        B = 1
        X = 2
        Y = 3
        GUIDE = 8 // was 12
        L_BUMPER = 4
        R_BUMPER = 5
        BACK = 6
        START = 7
        DPAD_UP = -1
        DPAD_DOWN = -1
        DPAD_LEFT = -1
        DPAD_RIGHT = -1
        L_STICK_PUSH = 9 // was 10
        R_STICK_PUSH = 10 // 12
        DPAD = 0
        L_TRIGGER_AXIS = 2
        R_TRIGGER_AXIS = 5
        R_TRIGGER = 11
        L_TRIGGER = 12
        L_STICK_VERTICAL_AXIS = 1
        L_STICK_HORIZONTAL_AXIS = 0
        R_STICK_VERTICAL_AXIS = 4
        R_STICK_HORIZONTAL_AXIS = 3
    }

    fun setToDS4() {
        mapping = "DS4"
        A = 1
        B = 2
        X = 0
        Y = 3
        GUIDE = 12
        L_BUMPER = 4
        R_BUMPER = 5
        BACK = 8
        START = 9
        DPAD_UP = -1
        DPAD_DOWN = -1
        DPAD_LEFT = -1
        DPAD_RIGHT = -1
        L_STICK_PUSH = 10
        R_STICK_PUSH = 11

        DPAD = 0

        L_TRIGGER_AXIS = 5
        R_TRIGGER_AXIS = 4

        R_TRIGGER = 7
        L_TRIGGER = 6

        L_STICK_VERTICAL_AXIS = 2

        L_STICK_HORIZONTAL_AXIS = 3

        R_STICK_VERTICAL_AXIS = 0

        R_STICK_HORIZONTAL_AXIS = 1
    }

    fun setToX360() {
        mapping = "Xbox 360"
        A = 0
        B = 1
        X = 2
        Y = 3
        GUIDE = 12
        L_BUMPER = 4
        R_BUMPER = 5
        BACK = 6
        START = 7
        DPAD_UP = -1
        DPAD_DOWN = -1
        DPAD_LEFT = -1
        DPAD_RIGHT = -1
        L_STICK_PUSH = 8 // was 10, changed for wii u pad
        R_STICK_PUSH = 9 // was 11

        DPAD = 0

        L_TRIGGER_AXIS = 4
        R_TRIGGER_AXIS = 5 // doesnt work on 360 pad, both are same axis
        crippledTrigger = true

        R_TRIGGER = 7 // doesnt work on 360 pad
        L_TRIGGER = 6 // doesnt work on 360 pad

        L_STICK_VERTICAL_AXIS = 0

        L_STICK_HORIZONTAL_AXIS = 1

        R_STICK_VERTICAL_AXIS = 2

        R_STICK_HORIZONTAL_AXIS = 3
    }

    fun setToF310() {
        mapping = "Logitech F310"
        A = 0
        B = 1
        X = 2
        Y = 3
        GUIDE = -1
        L_BUMPER = 4
        R_BUMPER = 5
        BACK = 6
        START = 7
        DPAD_UP = -1
        DPAD_DOWN = -1
        DPAD_LEFT = -1
        DPAD_RIGHT = -1
        L_STICK_PUSH = 8
        R_STICK_PUSH = 9

        DPAD = 0

        // TRIGGER_AXIS = 4

        R_TRIGGER = -1
        L_TRIGGER = -1

        L_STICK_VERTICAL_AXIS = 0

        L_STICK_HORIZONTAL_AXIS = 1

        R_STICK_VERTICAL_AXIS = 2

        R_STICK_HORIZONTAL_AXIS = 3
    }

    fun setToDirectX() {
        mapping = "DirectX controller"
        A = 1
        B = 2
        X = 0
        Y = 3
        GUIDE = -1
        L_BUMPER = 4
        R_BUMPER = 5
        BACK = 8
        START = 9
        DPAD_UP = -1
        DPAD_DOWN = -1
        DPAD_LEFT = -1
        DPAD_RIGHT = -1
        L_STICK_PUSH = 10 // f310, was 8
        R_STICK_PUSH = 11 // f310, was9

        DPAD = 0

        // TRIGGER_AXIS = 4

        R_TRIGGER = 7
        L_TRIGGER = 6

        L_STICK_VERTICAL_AXIS = 2

        L_STICK_HORIZONTAL_AXIS = 3

        R_STICK_VERTICAL_AXIS = 0

        R_STICK_HORIZONTAL_AXIS = 1
    }

    init {
        if (controller.name.startsWith("SDL")){
            setToSDLDefault()
        }
        else if (isOSX) {
            setToMacDefault()
        } else if (isLinux) {
            if (controller.name.contains("Sony")) {
                setToLinuxDefault()
            } else if (controller.name.contains("X-Box")) {
                setToLinuxXbox()
            } else {
                setToLinuxDefault()
            }
        } else { // isWindows
            if (controller.name.contains("F310")) {
                setToF310()
            } else if (controller.name.contains("Logitech Dual Action")) {
                setToDirectX()
            } else if (controller.name.contains("Wireless Controller")) {
                setToDS4()
            } else {
                setToX360()
            }
        }
    }

    fun b(): Boolean {
        return controller.getButton(B)
    }

    fun a(): Boolean {
        return controller.getButton(A)
    }

    fun x(): Boolean {
        return controller.getButton(X)
    }

    fun y(): Boolean {
        return controller.getButton(Y)
    }

    fun lBumper(): Boolean {
        return controller.getButton(L_BUMPER)
    }

    fun rBumper(): Boolean {
        return controller.getButton(R_BUMPER)
    }

    fun lTrigger(): Boolean {
        return controller.getButton(L_TRIGGER)
    }

    fun rTrigger(): Boolean {
        return controller.getButton(R_TRIGGER)
    }

    fun LStickHorizontalAxis(): Float {
        if (combinedDpad && DPAD != -1) {
            val d = controller.getPov(DPAD)
            if (d == PovDirection.east || d == PovDirection.northEast || d == PovDirection.southEast) {
                return 1f
            } else if (d == PovDirection.west || d == PovDirection.northWest || d == PovDirection.southWest) {
                return -1f
            } else if (d == PovDirection.north || d == PovDirection.south) {
                return 0f
            }
        }
        return controller.getAxis(L_STICK_HORIZONTAL_AXIS)
    }

    fun LStickVerticalAxis(): Float {
        if (combinedDpad && DPAD != -1) {
            val d = controller.getPov(DPAD)
            if (d == PovDirection.north || d == PovDirection.northEast || d == PovDirection.northWest) {
                return -1f
            } else if (d == PovDirection.south || d == PovDirection.southEast || d == PovDirection.southWest) {
                return 1f
            } else if (d == PovDirection.east || d == PovDirection.west) {
                return 0f
            }
        }
        return controller.getAxis(L_STICK_VERTICAL_AXIS)
    }

    fun RStickHorizontalAxis(): Float {
        return controller.getAxis(R_STICK_HORIZONTAL_AXIS)
    }

    fun RStickVerticalAxis(): Float {
        return controller.getAxis(R_STICK_VERTICAL_AXIS)
    }

    fun leftTrigger(): Float {
        // log("mappedcontroller","$L_TRIGGER_AXIS $L_TRIGGER ${controller.getAxis(L_TRIGGER_AXIS)}")
        if (crippledTrigger) {
            return (controller.getAxis(L_TRIGGER_AXIS) * 2) - 1
        } else {
            return controller.getAxis(L_TRIGGER_AXIS)
        }
    }

    fun rightTrigger(): Float {
        if (crippledTrigger) {
            return (controller.getAxis(L_TRIGGER_AXIS) * -2) - 1
        } else {
            return controller.getAxis((R_TRIGGER_AXIS))
        }
    }

    fun start(): Boolean {
        return controller.getButton(START)
    }

    //var listener: ControllerAdapter? = null
}