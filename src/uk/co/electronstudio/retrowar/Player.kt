package uk.co.electronstudio.retrowar

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import uk.co.electronstudio.retrowar.input.InputDevice

/**
 * Represents a player who has joined the game
 * @param input The keyboard/controller etc he is using
 * @param name The name to display for this player.  Defaults to 'PLAYERx' but may be customized in options
 * @param color A custom colour chosen by player in options
 * @param color Another custom colour
 */
open class Player(
        @Transient val input: InputDevice,
        val name: String,
        val color: Color,
        val color2:
        Color
) :
        Comparable<Player> {

    /**
     * Compares players to see who has highest score
     */
    override fun compareTo(other: Player): Int {
        return score.compareTo(other.score)
    }

    /**
     * If your game uses entities you can store the one representing the Player's sprite here.
     */
    //var entityId: Int = -1

    /**
     * If your game keeps score you can store it here
     */
    var score: Int = 0

    /**
     * If your game tracks lives, you can store how many times the player has died here
     * (rather than lives remaining)
     */
    var deaths: Int = 0

    /**
     * If your game tracks health, you can store how much health the player has lost here
     * (rather than storing the remaining health)
     */
    var healthLost: Float = 0f

    /**
     * When game is part of a multi-round tournment this represents the overall score in
     * the tournament
     */
    var metaScore: Int = 0
        private set(value) {field = value}

    fun incMetaScore(i: Int=1){
        metaScore+=i
    }

    /**
     * How much health the player started with
     */
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

    /**
    * Gameover, man, gameover.
     */
    fun isOutOfLives(lives: Int): Boolean {
        return deaths >= lives
    }

    /**
    * Resets score, lives and health for a new round
     */
    fun reset() {
        score = 0
        deaths = 0
        healthLost = 0f
    }
}
