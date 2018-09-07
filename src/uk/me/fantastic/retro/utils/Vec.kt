package uk.me.fantastic.retro.utils

/**
 * 2d vector
 */
class Vec(val x: Float, val y: Float) {
    constructor() : this(0f, 0f)

    fun xy(): Pair<Float, Float> {
        return Pair(x, y)
    }

    fun normVector(): Vec {

        val vMagnitude = (x * x + y * y).sqrt()
        return Vec(x / vMagnitude, y / vMagnitude)
    }

    infix operator fun minus(p: Vec): Vec = Vec(x - p.x, y - p.y)
}