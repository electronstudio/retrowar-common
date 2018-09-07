package uk.me.fantastic.retro

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import uk.me.fantastic.retro.input.InputDevice

/**
 *
 */
open class Player(
        @Transient val input: InputDevice,
        val name: String,
        val color: Color,
        val color2:
        Color
) :
        Comparable<Player> {

    override fun compareTo(other: Player): Int {
        return score.compareTo(other.score)
    }

    var entityId: Int = -1 // might not be used by most games but convenient for those that use it to have it here

    var score: Int = 0
    var deaths: Int = 0
    var healthLost: Float = 0f

    var metaScore: Int = 0
        private set(value) {field = value}

    fun incMetaScore(i: Int=1){
        metaScore+=i
    }

    var startingHealth: Float = 0f

    fun healthDisplayString(startingHealth: Float): String {
        val healthLeft = MathUtils.clamp((startingHealth - healthLost).toInt(), 0, 10)
        val health = "#".repeat(healthLeft)
        return health
    }

    fun livesDisplayString(startingLives: Int): String {
        val livesLeft = MathUtils.clamp(startingLives - deaths, 0, 10)

        val lives = "*".repeat(livesLeft)

        return lives
    }

    fun isOutOfLives(lives: Int): Boolean {
        return deaths >= lives
    }

    fun reset() {
        score = 0
        deaths = 0
        healthLost = 0f
    }

    // constructor() : this(null, "", -1, Color.WHITE)
}
