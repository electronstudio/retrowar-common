package uk.me.fantastic.retro.input

import uk.me.fantastic.retro.utils.Vec

/**
 * All input devices are abstracted to look something like the ubiquitious xbox controller
 */
abstract class InputDevice {

    abstract val leftStick: Vec
    abstract val rightStick: Vec
    abstract val leftTrigger: Float
    abstract val rightTrigger: Float

    abstract val A: Boolean
    abstract val B: Boolean
    abstract val X: Boolean
    abstract val Y: Boolean

    abstract val leftBumper: Boolean
    abstract val rightBumper: Boolean

    val fire: Boolean
        get() {
            return (A || B || X || Y) || rightBumper
        }

    var entity: Int = -1

    internal fun filterDeadzone(deadzone: Float, axisX: Float, axisY: Float): Pair<Float, Float> {
        if (axisX < deadzone && axisX > -deadzone && axisY < deadzone && axisY > -deadzone) {
            return Pair(0f, 0f)
        }
        return Pair(axisX, axisY)
    }
}
