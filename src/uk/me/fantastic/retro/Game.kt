package uk.me.fantastic.retro

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import uk.me.fantastic.retro.screens.GameSession
import uk.me.fantastic.retro.utils.Vec

/**
 * top the hierarchy, most abstract kind of Game we support.  if you implement this you will do most everything youself.
 * most games probably want to implement SimpleGame subclass instead to get menus.
 */
abstract class Game(val session: GameSession) {

    interface UsesMouseAsInputDevice {
        fun getMouse(): Vec
    }

    open val MAX_FPS = 1000f
    open val MIN_FPS = 10f

    val players: ArrayList<Player>
        get() = session.players

    open fun gameover() {
        session.quit()
    }

    abstract fun show()
    abstract fun hide()

    abstract fun resize(width: Int, height: Int)
    abstract fun render(deltaTime: Float)
    abstract fun postMessage(s: String)

    abstract val renderer: FBORenderer
    abstract fun dispose()
    fun renderAndClampFramerate() {
        val delta = MathUtils.clamp(Gdx.graphics.rawDeltaTime, 1f / MAX_FPS, 1f / MIN_FPS)
        render(delta)
    }
}