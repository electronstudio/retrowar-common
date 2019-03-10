package uk.co.electronstudio.retrowar

import com.badlogic.gdx.graphics.Texture
import uk.co.electronstudio.retrowar.screens.GameSession

/**
 * produces Games.  you may want to subclass this for your own Game, but you may be able
 * use it as-is by passing in your own constructor method
 */
open class GameFactory(name: String, val createGame: (GameSession) -> Game, val i: Texture? = null) :
    AbstractGameFactory(name) {

    override val description: String = name

    override val image: Texture
        get() {
            if (i != null) {
                return i
            } else {
                return Resources.MISSING_TEXTURE
            }
        }

    override fun create(session: GameSession): Game {
        return createGame(session)
    }
}
