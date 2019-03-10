package uk.co.electronstudio.retrowar

import com.badlogic.gdx.graphics.Texture
import uk.co.electronstudio.retrowar.screens.GameSession

class SimpleGameFactory(
    name: String,
    private val gameClazz: Class<out Game>,
    path: String,
    override val image: Texture = Resources.MISSING_TEXTURE,
    override val description: String = name
) : AbstractGameFactory(name = name, pathPrefix = path) {

    override fun create(session: GameSession): Game {
        return gameClazz.getConstructor(GameSession::class.java, String::class.java).newInstance(session, pathPrefix)
    }
}