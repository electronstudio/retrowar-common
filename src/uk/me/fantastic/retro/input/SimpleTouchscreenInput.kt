package uk.me.fantastic.retro.input

import com.badlogic.gdx.Gdx
import uk.me.fantastic.retro.utils.Vec

/**
 * has one joystick on the left and one button on the right of the screen
 */
internal class SimpleTouchscreenInput : InputDevice() {

    var joyStickOrigin = Vec(0f, 0f)
    var joyStickPosition = Vec(0f, 0f)
    var joyStickFinger = -1

    override val leftTrigger: Float
        get() = 0f
    override val rightTrigger: Float
        get() = 0f

    override val A: Boolean
        get() {
//            if (TouchscreenJoystick.touchReleased) {
//                TouchscreenJoystick.touchReleased = false
//                return true
//            }
            for (i in 0..10) {
                if (Gdx.input.isTouched(i)) {
                    val x = Gdx.input.getX(i).toFloat()
                    // val y = Gdx.input.getY(i).toFloat()
                    if (x > Gdx.graphics.displayMode.width / 2) {
                        return true
                    }
                }
            }
            return false
        }

    override val leftStick: Vec
        get() {
            for (i in 0..10) {
                if (Gdx.input.isTouched(i)) {
                    val x = Gdx.input.getX(i).toFloat()
                    val y = Gdx.input.getY(i).toFloat()
                    if (x < Gdx.graphics.displayMode.width / 2) {
                        if (joyStickFinger == -1) {
                            joyStickOrigin = Vec(x, y)
                        }
                        joyStickFinger = i
                        joyStickPosition = Vec(x, y)
                        val (a, b) = filterDeadzone(0f, (joyStickPosition.x - joyStickOrigin.x), (joyStickPosition.y - joyStickOrigin.y))
                        return Vec(a, b).normVector()
                    }
                }
            }
            joyStickFinger = -1
            return Vec(0f, 0f)
        }

    override val rightStick: Vec
        get() {

            return Vec(0f, 0f)
        }
    override val B: Boolean
        get() {
            return false
        }
    override val X: Boolean
        get() {
            return false
        }
    override val Y: Boolean
        get() {
            return false
        }
    override val leftBumper: Boolean
        get() {
            return false
        }
    override val rightBumper: Boolean
        get() {
            return false
        }
}
