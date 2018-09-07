package uk.me.fantastic.retro.input

/**
 * AI controlled, not very smart, more like drunk input!
 */
// class RobotInput(val game: GameMappers) : InputDevice() {
//    override val B: Boolean
//        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//    override val X: Boolean
//        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//    override val Y: Boolean
//        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//    override val leftBumper: Boolean
//        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//    override val rightBumper: Boolean
//        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
//    override val leftTrigger: Float
//        get() = 0f
//    override val rightTrigger: Float
//        get() = 0f
//
//
//    // FIXME array is not the best for a circular list I know
//    val waypoints = arrayOf(Pair(350f, 200f), Pair(100f, 200f), Pair(100f, 100f), Pair(350f, 100f))
//
//    var waypoint = 0
//
//    override val leftStick: Vec
//        get() {
//
//            val destination = waypoints[waypoint]
//            val (x, y) = destination
//
//            val position = game.positionMapper[entity]
//
//            if (position!!.isCloseTo(destination)) {
//                waypoint++
//                if (waypoint >= waypoints.size) {
//                    waypoint = 0
//                }
//            }
//
//            val dx = x - position.x
//            val dy = y - position.y
//
//            val nx = clamp(dx, -1f, 1f)
//            val ny = clamp(dy, -1f, 1f)
//
//            return Vec(nx, -ny)
//        }
//
//    override val rightStick: Vec
//        get() {
//
//            return Vec()
//        }
//
//    override val A: Boolean
//        get() {
//            return false
//        }
//
//    fun clamp(x: Float, min: Float, max: Float): Float {
//        return Math.max(min, Math.min(max, x))
//    }
// }
