package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import uk.co.electronstudio.retrowar.menu.MenuController
import uk.co.electronstudio.retrowar.screens.GameSession

/**
 * Most games probably want to extend this.  It does menus and rendering loop, but it's not a Unigame so you're still
 * free to do any sort of game you want really.
 */
abstract class SimpleGame @JvmOverloads constructor(
    session: GameSession,
    val width: Float,
    val height: Float,
    // val fontClear: BitmapFont = Resources.FONT_CLEAR,
    val font: BitmapFont = Resources.FONT_CLEAR,
    val fadeInEffect: Boolean = true
) : Game(session) {

    override val renderer = FBORenderer(WIDTH = width, HEIGHT = height, fadeInEffect = fadeInEffect)

    private val controller = MenuController(session.standardMenu(), width, height, font, x = 0f, y = height - 4)

    /** note that GameSession can last for multiple (Simple)Games so this is different from players in Session*/
    val playersInGame: ArrayList<Player> = ArrayList()

    init {
        font.data.markupEnabled = true
        //   fontClear.data.markupEnabled = true
    }



    // render is called by libgdx once every frame (required)
    override fun render(deltaTime: Float) {

        /* Bring the playersInGame up to sync with players in session */
        for(player in players){
            if(!playersInGame.contains(player)){
                playersInGame.add(player)
                playerJoined(player)
            }
        }
/**
        jump multipler
 coin multipler
 time multipler
 jump chain bonus multiplery
 speed up + attack + invuln for last player
 all games to support disconnects/reconnects - session notify them directly?
 time speeds up after each player exits
        */

        val deadPlayers = playersInGame.filter { !players.contains(it) }
        deadPlayers.forEach {
            playersInGame.remove(it)
            playerLeft(it)
        } // JDK 8 / Android 7: removeIf

        doLogic(deltaTime)

        val batch = renderer.beginFBO()
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        doDrawing(batch)

        batch.end()

        if (session.state == GameSession.GameState.MENU) {
            batch.begin()
            drawMenu(batch)
            batch.end()
        }

        renderer.renderFBOtoScreen()
    }

    open fun playerJoined(player: Player) {
    }

    open fun playerLeft(player: Player) {
    }

    fun simpleHighScoreTable(): String = players.sortedDescending().joinToString("") {
        (if(players.size<7) "\n" else "")+"\n${it.name} ${it.score}"
    }

    abstract fun doDrawing(batch: Batch)

    abstract fun doLogic(deltaTime: Float)

    private fun drawMenu(batch: Batch) {
        controller.doInput()
        val mouse = renderer.convertScreenToGameCoords(Gdx.input.x, Gdx.input.y)
        controller.doMouseInput(mouse.x, mouse.y)
        controller.draw(batch)
    }

    override fun resize(width: Int, height: Int) {
        log("retrogame resize " + toString())
        renderer.resize(width, height)
    }

    override fun postMessage(s: String) {
    }
}