package uk.co.electronstudio.retrowar.input

import uk.co.electronstudio.retrowar.utils.Vec

/**
 * Created by Richard on 13/08/2016.
 * Maps a controller to an input
 */
internal class GamepadInput(val controller: MappedController) : InputDevice() {

    var singleStickMode = true
    var lockedFireDirection:Vec? = null

    override val leftStick: Vec
        get() {
            return filterDeadzone(0.05f, controller.LStickHorizontalAxis(), controller.LStickVerticalAxis())
        }

    override val rightStick: Vec
        get() {
           // if(controller.RStickHorizontalAxis()!=0f || controller.RStickVerticalAxis()!=0f) singleStickMode=false

            val rawData = filterDeadzone(0.6f, controller.RStickHorizontalAxis(), controller.RStickVerticalAxis())
            if(rawData.isMoreOrLessZero()) {
                if(controller.rBumper() || controller.a()) {
                    if(lockedFireDirection==null) {
                        lockedFireDirection = leftStick
                    }
                    return lockedFireDirection!!
                }else{
                    lockedFireDirection = null
                    return Vec(0f,0f)
                }
            }else {
                return rawData
            }
        }

    override val leftTrigger: Float
        get() {
            //  log("GamepadInput ${controller.leftTrigger()}")
            return controller.leftTrigger()
        }
    override val rightTrigger: Float
        get() {
            return controller.rightTrigger()
        }

    override val A: Boolean
        get() {
            return controller.a()
        }

    override val B: Boolean
        get() {
            return controller.b()
        }

    override val X: Boolean
        get() {
            return controller.x()
        }

    override val Y: Boolean
        get() {
            return controller.y()
        }

    override val leftBumper: Boolean
        get() {
            return controller.lBumper()
        }

    override val rightBumper: Boolean
        get() {
            return controller.rBumper()
        }
}
