package uk.co.electronstudio.retrowar.utils

import com.badlogic.gdx.math.MathUtils

/**
 * 2d vector.  Seems wasteful to create millions of these, but they should be removed by JVM's escape analysis.
 * In some places we have used other equivalents like Pair<Float, Float>, and this can convery to that, but I
 * think we should standardise.  FIXME
 */
class Vec(val x: Float, val y: Float) {
    constructor() : this(0f, 0f)

    companion object{
        inline fun fromPolar(degrees: Float, magnitude: Float): Vec{
            val x = MathUtils.sinDeg(degrees) * magnitude
            val y = MathUtils.cosDeg(degrees) * magnitude
            return Vec(x,y)
        }
    }

    fun xy(): Pair<Float, Float> {
        return Pair(x, y)
    }

    operator fun component1() = x
    operator fun component2() = y

    fun normVector(magnitude: Float = 1f): Vec? {
        val oldMagnitude = magnitude()
        if(oldMagnitude < 0.000000001f) return null
        return Vec(x / oldMagnitude, y / oldMagnitude) * magnitude
    }

    fun normVectorOrZero(magnitude: Float = 1f): Vec {
       return normVector(magnitude) ?: Vec(0f,0f)
    }

    fun magnitude(): Float {
        return (x * x + y * y).sqrt()
    }

    infix operator fun minus(p: Vec): Vec = Vec(x - p.x, y - p.y)

    fun isMoreOrLessZero(): Boolean {
        return (x < 0.001f && x > -0.001f && y < 0.001f && y > -0.001f)
    }

    fun isNotZero() = !isMoreOrLessZero()

    override infix operator fun equals(other: Any?): Boolean {
        if (other is Vec) {
            if (MathUtils.isEqual(x, other.x) && (MathUtils.isEqual(y, other.y))) return true
        }
        return false
    }

    operator fun times(other: Float): Vec {
        return Vec(this.x*other, this.y*other)
    }

    fun rescaleWithDeadzone(deadzone: Float): Vec {
        val magnitude = magnitude()
        if (magnitude<deadzone) {
            return Vec(0f, 0f)
        } else {
            val range = 1f - deadzone
            val scaleFactor = (magnitude - deadzone) / range
            return normVectorOrZero() * scaleFactor
        }
    }

    fun ignoreDeadzone(deadzone: Float): Vec {
        if (magnitude()<deadzone) return Vec(0f, 0f)
        else return this
    }

    fun clampMagnitude(max: Float): Vec =
        if (magnitude()>max)
            normVector(magnitude = max) ?: this
        else
            this

    fun rescaleWithDeadzone(deadzone: Float, upperBound: Float): Vec {
        val magnitude = magnitude()
        if (magnitude <= deadzone) {
            return Vec(0f, 0f)
        } else if (magnitude>upperBound) {
            return normVectorOrZero(magnitude = upperBound)
        } else {
            val range = upperBound - deadzone
            val scaleFactor = (magnitude - deadzone) / range
            return normVectorOrZero() * scaleFactor
        }
    }

    fun toAngle(): Float {
        return MathUtils.atan2(x, y)
    }

    fun convertToDpad(): Vec {
        val n = this.normVectorOrZero()
        return Vec(if (n.x>0.38) 1f else if (n.x < -0.38f) -1f else 0f, if (n.y>0.38) 1f else if (n.y < -0.38f) -1f else 0f)
    }

    override fun toString() = "Vec($x, $y)"
}