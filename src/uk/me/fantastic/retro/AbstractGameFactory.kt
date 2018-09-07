package uk.me.fantastic.retro

import com.badlogic.gdx.graphics.Texture
import uk.me.fantastic.retro.menu.MultiChoiceMenuItem
import uk.me.fantastic.retro.screens.GameSession

/**
 * GameFactories provide RetroWar with info about a game and generate an
 * instance of the game on demand, applying any configuration that has been
 * stored in the factory.
 *
 * If you are making a stand-alone SimpleGame you don't need this, just create
 * a SimpleGameFactory.  But if you are making a plugin for RetroWar then you need to write
 * your own subclass of AbstractGameFactory.
 *
 * @property name Game name
 * @property levels a List of level names, only for games that have multiple levels. May be null.
 */
abstract class AbstractGameFactory(val name: String, val levels: List<String>? = null) {

    /** Currently selected level number */
    var level = 0

    /** Whether the game should be shown on the main menu or relegated to the 'mods' menu */
    var showOnGamesMenu = true

    /** Texture screenshot or logo to display on menu */
    abstract val image: Texture

    /** Description displayed on menu */
    abstract val description: String

    abstract fun create(session: GameSession): Game

    /** For mult-game tournaments ignore the settings in this factory and create a game with some
     * defaults appropriate for a tournament */
    open fun createWithDefaultSettings(session: GameSession): Game {
        return create(session)
    }

    /**
     * Any MenuItems in this List will be displayed by RetroWar on an option screen
     * It's a way to configure the Factory via a GUI
     * If there are none, just leave List empty
     */
    open val options: List<MultiChoiceMenuItem> = ArrayList<MultiChoiceMenuItem>()
}