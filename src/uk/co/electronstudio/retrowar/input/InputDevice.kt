package uk.co.electronstudio.retrowar.input

import uk.co.electronstudio.retrowar.utils.Vec

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

    internal fun filterDeadzone(deadzone: Float, axisX: Float, axisY: Float): Vec {
        if (axisX < deadzone && axisX > -deadzone && axisY < deadzone && axisY > -deadzone) {
            return Vec(0f, 0f)
        }
        return Vec(axisX, axisY)
    }

    fun dPadDirection(): Vec {
        var x = 0f
        var y = 0f
        if (leftStick.x > 0.3) x = 1f
        else if (leftStick.x < -0.3) x = -1f
        if (leftStick.y > 0.3) y = 1f
        else if (leftStick.y < -0.3) y = -1f
        return Vec(x, y)
    }
}
