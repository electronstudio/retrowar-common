package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import uk.co.electronstudio.retrowar.screens.GameSession
import uk.co.electronstudio.retrowar.utils.Vec

/**
 * top of the hierarchy, most abstract kind of Game we support.  if you implement this you will do most everything
 * youself.
 * most games probably want to implement SimpleGame subclass instead to get menus.
 */
abstract class Game(val session: GameSession) {

    /**
     * For games that require mouse input, we dont supply the raw mouse co-ordinates, we convert them to
     * joystick vector.  However to do this we need help from the game, so it must implement this interface.
     */
    interface UsesMouseAsInputDevice {
        /**
         * Get mouse co-ords from GDX input and convert them into a joystick vector
         */
        fun getMouse(player: Player): Vec
        fun getMouseFromGameCoords(player: Player, x: Float, y: Float) : Vec
        fun getMouseFromGameCoordsFlipY(player: Player, x: Float, y: Float): Vec
    }

    /* Maximum FPS we can handle, useful so physics doesnt break at extreme framerates */
    open val MAX_FPS = 1000f

    /* Minimum FPS we can handle, useful so physics doesnt break at extreme framerates */
    open val MIN_FPS = 10f

    /* All players currently in the game */
    val players: ArrayList<Player>
        get() = session.players

    /* Call when game is finished */
    open fun gameover() {
        session.quit()
    }

    abstract fun show()
    abstract fun hide()

    abstract fun resize(width: Int, height: Int)

    /* Do all your drawing here */
    abstract fun render(deltaTime: Float)

    /* Supposed to be for displaying messages to the player, dont think most games implement this? */
    abstract fun postMessage(s: String)

    abstract val renderer: FBORenderer

    /* Called to release resouces when we are done */
    abstract fun dispose()

    /* Calls render but doesnt allow extreme framerates, i.e. physics will slow down rather than break and allow
    * players to walk through walls.
     */
    fun renderAndClampFramerate() {
        val delta = MathUtils.clamp(Gdx.graphics.rawDeltaTime, 1f / MAX_FPS, 1f / MIN_FPS)
        render(delta)
    }
}
