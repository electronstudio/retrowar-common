package uk.me.fantastic.retro.utils

/**
 * 2d vector.  Seems wasteful to create millions of these, but they should be removed by JVM's escape analysis.
 * In some places we have used other equivalents like Pair<Float, Float>, and this can convery to that, but I
 * think we should standardise.  FIXME
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
    fun isMoreOrLessZero(): Boolean {
        return (x<0.001f && x>-0.001f && y<0.001f && y>-0.001f)
    }
}