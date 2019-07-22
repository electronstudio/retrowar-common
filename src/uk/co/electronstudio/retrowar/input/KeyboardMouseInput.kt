package uk.co.electronstudio.retrowar.input

import com.badlogic.gdx.Input.*
import uk.co.electronstudio.retrowar.Game
import uk.co.electronstudio.retrowar.screens.GameSession

import uk.co.electronstudio.retrowar.utils.Vec
import com.badlogic.gdx.Gdx.input
import sun.audio.AudioPlayer.player


/**
 * Maps Keyboard and mouse to input
 */
internal class KeyboardMouseInput(val session: GameSession) : InputDevice() {

    override val leftTrigger: Float
        get() = if(input.isButtonPressed(Buttons.MIDDLE) || input.isKeyPressed(Keys.SHIFT_LEFT)  || input.isKeyPressed(Keys.Z) ) 1f else 0f
    override val rightTrigger: Float
        get() = if(input.isButtonPressed(Buttons.RIGHT)|| input.isKeyPressed(Keys.SHIFT_RIGHT)  || input.isKeyPressed(Keys.SLASH)  ) 1f else 0f

    override val movementVec: Vec
        get() {
            var x = 0f
            var y = 0f
            if (input.isKeyPressed(Keys.A) || input.isKeyPressed(Keys.NUMPAD_4)) {
                x = -1f
            }
            if (input.isKeyPressed(Keys.D) || input.isKeyPressed(Keys.NUMPAD_6)) {
                x = 1f
            }
            if (input.isKeyPressed(Keys.W) || input.isKeyPressed(Keys.NUMPAD_8)) {
                y = -1f
            }
            if (input.isKeyPressed(Keys.S) || input.isKeyPressed(Keys.NUMPAD_2)) {
                y = 1f
            }
            return Vec(x, y).clampMagnitude(1f)
        }

    override val A: Boolean
        get() {
            return input.isKeyPressed(Keys.SPACE) || input.isButtonPressed(0)
        }
    override val B: Boolean
        get() {
            return input.isKeyPressed(Keys.ENTER)
        }
    override val X: Boolean
        get() {
            return input.isKeyPressed(Keys.CONTROL_LEFT)
        }
    override val Y: Boolean
        get() {
            return input.isKeyPressed(Keys.CONTROL_RIGHT)
        }

    override val leftBumper: Boolean
        get() {
            return input.isKeyPressed(Keys.TAB) // || input.isButtonPressed(Keys.TAB)
        }
    override val rightBumper: Boolean
        get() {
            return input.isKeyPressed(Keys.E)
        }
    //  val pointers = Aspect.all(IsPointer::class.java)

    override val aimingVec: Vec
        get() {
            if (pressed(Keys.UP) || pressed(Keys.DOWN) || pressed(Keys.LEFT) || pressed(Keys.RIGHT)) {
                return keyboardAsRightStick()
            }

            //  if (!input.isButtonPressed(0)) return Vec(0f, 0f)
            //   if (input.isButtonPressed(Buttons.LEFT)) {
            //  val target = GameMappers.positionMapper.get(pointer)

            //    val playerV = (playerVelocity.x*playerVelocity.x+playerVelocity.y*playerVelocity.y).sqrt()

            if (!input.isButtonPressed(Buttons.LEFT) && !input.isButtonPressed(Buttons.RIGHT)) {
                return Vec(0f, 0f)
            }

            val game = session.game
            val player = this.player
            if (game != null && game is Game.UsesMouseAsInputDevice && player != null) {
                return  game.getMouse(player)
            }
            return Vec(0f, 0f)
        }

    private fun keyboardAsRightStick(): Vec {
        var x = 0f
        var y = 0f
        if (input.isKeyPressed(Keys.LEFT)) {
            x = -1f
        }
        if (input.isKeyPressed(Keys.RIGHT)) {
            x = 1f
        }
        if (input.isKeyPressed(Keys.UP)) {
            y = -1f
        }
        if (input.isKeyPressed(Keys.DOWN)) {
            y = 1f
        }
        return Vec(x, y).clampMagnitude(1f)
    }

    fun pressed(x: Int): Boolean = input.isKeyPressed(x)

    val <A, B> Pair<A, B>.x: A
        get() = first

    val <A, B> Pair<A, B>.y: B
        get() = second
}
