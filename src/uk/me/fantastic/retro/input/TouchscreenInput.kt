package uk.me.fantastic.retro.input

/**
 * Created by Richard on 13/08/2016.
 * Maps a the touchscreen controller system controller to an inputdevice so it can be read
 */
// class TouchscreenInput : InputDevice() {
//
//    override val leftTrigger: Float
//        get() = 0f
//    override val rightTrigger: Float
//        get() = 0f
//
//    override val A: Boolean
//        get() {
// //            if (TouchscreenJoystick.touchReleased) {
// //                TouchscreenJoystick.touchReleased = false
// //                return true
// //            }
//            return (Gdx.input.isTouched(1))
//           // return false
//        }
//
//    override val leftStick: Vec
//        get() {
//            val x = TouchscreenJoystick.LStickHorizontalAxis
//            val y = TouchscreenJoystick.LStickVerticalAxis
//            val (a, b) = filterDeadzone(0.15f, x, y)
//            return Vec(a, b)
//        }
//
//    override val rightStick: Vec
//        get() {
//            val x = TouchscreenJoystick.RStickHorizontalAxis
//            val y = TouchscreenJoystick.RStickVerticalAxis
//            val (a, b) = filterDeadzone(0.6f, x, y)
//            return Vec(a, b)
//        }
//    override val B: Boolean
//        get() {
//            return false
//        }
//    override val X: Boolean
//        get() {
//            return false
//        }
//    override val Y: Boolean
//        get() {
//            return false
//        }
//    override val leftBumper: Boolean
//        get() {
//            return false
//        }
//    override val rightBumper: Boolean
//        get() {
//            return false
//        }
// }
