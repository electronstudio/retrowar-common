package uk.me.fantastic.retro.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import uk.me.fantastic.retro.Game
import uk.me.fantastic.retro.screens.GameSession

import uk.me.fantastic.retro.utils.Vec

/**
 * Maps Keyboard and mouse to input
 */
internal class KeyboardMouseInput(val session: GameSession) : InputDevice() {

    override val leftTrigger: Float
        get() = 0f
    override val rightTrigger: Float
        get() = 0f

    override val leftStick: Vec
        get() {
            var x = 0f
            var y = 0f
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4)) {
                x = -1f
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_6)) {
                x = 1f
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)) {
                y = -1f
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2)) {
                y = 1f
            }
            return Vec(x, y)
        }

    override val A: Boolean
        get() {
            return Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isButtonPressed(0)
        }
    override val B: Boolean
        get() {
            return Gdx.input.isKeyPressed(Input.Keys.ENTER)
        }
    override val X: Boolean
        get() {
            return Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
        }
    override val Y: Boolean
        get() {
            return Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)
        }

    override val leftBumper: Boolean
        get() {
            return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
        }
    override val rightBumper: Boolean
        get() {
            return Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)
        }
    //  val pointers = Aspect.all(IsPointer::class.java)

    override val rightStick: Vec
        get() {
            if (pressed(Input.Keys.UP) || pressed(Input.Keys.DOWN) || pressed(Input.Keys.LEFT) || pressed(Input.Keys.RIGHT)) {
                return keyboardAsRightStick()
            }

            //  if (!Gdx.input.isButtonPressed(0)) return Vec(0f, 0f)
            //   if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            //  val target = GameMappers.positionMapper.get(pointer)

            //    val playerV = (playerVelocity.x*playerVelocity.x+playerVelocity.y*playerVelocity.y).sqrt()

            if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                return Vec(0f, 0f)
            }

            val game = session.game
            if (game != null && game is Game.UsesMouseAsInputDevice) {
                val m = game.getMouse()
                return m
            }
            return Vec(0f, 0f)
        }

    private fun keyboardAsRightStick(): Vec {
        var x = 0f
        var y = 0f
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x = -1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x = 1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            y = -1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y = 1f
        }
        return Vec(x, y)
    }

    fun pressed(x: Int): Boolean = Gdx.input.isKeyPressed(x)

    val <A, B> Pair<A, B>.x: A
        get() = first

    val <A, B> Pair<A, B>.y: B
        get() = second
}
