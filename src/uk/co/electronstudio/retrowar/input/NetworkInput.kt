package uk.co.electronstudio.retrowar.input

import uk.co.electronstudio.retrowar.utils.Vec

/**
 * stores state of a device (FIXME REMOVE CLIENTID?)
 * to be sent over network
 */
open class NetworkInput(
    override var movementVec: Vec = Vec(0f, 0f),
    override var aimingVec: Vec = Vec(0f, 0f),
    override var leftTrigger: Float = 0f,
    override var rightTrigger: Float = 0f,
    override var A: Boolean = false,
    val clientId: Int = -1
) : InputDevice() {
    override val B: Boolean
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
    override val X: Boolean
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
    override val Y: Boolean
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
    override val leftBumper: Boolean
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.
    override val rightBumper: Boolean
        get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.

    fun copyTo(other: NetworkInput) {
        other.movementVec = movementVec
        other.aimingVec = aimingVec
        other.A = A
    }

    fun copyFrom(other: InputDevice) {
        movementVec = other.movementVec
        aimingVec = other.aimingVec
        A = other.A
    }
}
