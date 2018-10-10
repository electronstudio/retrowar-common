package uk.me.fantastic.retro

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import uk.me.fantastic.retro.screens.GameSession

class SimpleGameFactory(name: String, private val gameClazz: Class<out Game>, path: String, override val image:
Texture = Resources.MISSING_TEXTURE) :
        AbstractGameFactory
(name = name, pathPrefix = path) {

    override val description = name


    override fun create(session: GameSession): Game {
        return gameClazz.getConstructor(GameSession::class.java, String::class.java).newInstance(session, pathPrefix)
    }
}