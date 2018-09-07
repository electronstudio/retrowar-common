package uk.me.fantastic.retro.input

import uk.me.fantastic.retro.utils.Vec

/**
 * Created by Richard on 13/08/2016.
 * Maps a controller to an input
 */
internal class GamepadInput(val controller: MappedController) : InputDevice() {

    override val leftStick: Vec
        get() {
            val x = controller.LStickHorizontalAxis()
            val y = controller.LStickVerticalAxis()
            val (a, b) = filterDeadzone(0.05f, x, y)
            return Vec(a, b)
        }

    override val rightStick: Vec
        get() {
            val x = controller.RStickHorizontalAxis()
            val y = controller.RStickVerticalAxis()
            val (a, b) = filterDeadzone(0.6f, x, y)
            return Vec(a, b)
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
