package uk.co.electronstudio.retrowar.input

import uk.co.electronstudio.retrowar.utils.Vec

/**
 * All input devices are abstracted to look something like the ubiquitious xbox controller
 */
abstract class InputDevice {
    open fun rumble(left: Float, right: Float, duration_ms: Int) {

    }

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

//    internal fun filterDeadzone(deadzone: Float = 0.1f, axisX: Float, axisY: Float, outerCircle: Float = 1f): Vec {
//        val v = Vec(axisX, axisY)
//        val magnitude=v.magnitude()
//        if (magnitude<=deadzone) {
//            return Vec(0f, 0f)
//        }else if(magnitude>outerCircle){
//            return v.normVector(magnitude = outerCircle)
//           //val scale = outerCircle/m
//           // return v * scale
//        }else{
//            val scale = (magnitude-deadzone)/(outerCircle-deadzone)
//            return v.normVector() * scale
//        }
//    }


}
