package uk.me.fantastic.retro

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import uk.me.fantastic.retro.screens.GameSession

class SimpleGameFactory(name: String, private val gameClazz: Class<out Game>) : AbstractGameFactory(name = name) {

    override val description = name

    override fun create(session: GameSession): Game {
        return gameClazz.getConstructor(GameSession::class.java).newInstance(session)
    }
}