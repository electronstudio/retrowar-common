package uk.me.fantastic.retro

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import uk.me.fantastic.retro.menu.MenuController
import uk.me.fantastic.retro.screens.GameSession

/**
 * Most games probably want to extend this.  It does menus and rendering loop, but it's not a Unigame so you're still
 * free to do any sort of game you want really.
 */
abstract class SimpleGame(
    session: GameSession,
    val width: Float,
    val height: Float,
    //val fontClear: BitmapFont = Resources.FONT_CLEAR,
    val font: BitmapFont = Resources.FONT,
    val fadeInEffect: Boolean = true
) : Game(session) {

    override val renderer = FBORenderer(WIDTH = width, HEIGHT = height, fadeInEffect = fadeInEffect)

    private val controller = MenuController(session.standardMenu(), width, height, font, x = 0f, y = height - 4)

    var noOfPlayersInGameAlready=0

    init {
        font.data.markupEnabled = true
     //   fontClear.data.markupEnabled = true
    }

    // render is called by libgdx once every frame (required)
    override fun render(deltaTime: Float) {

        for (i in noOfPlayersInGameAlready until players.size) { // loop only when there is a new player(s) joined
            noOfPlayersInGameAlready++
            playerJoined(players[i])
        }

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

    fun simpleHighScoreTable(): String = players.sortedDescending().joinToString("") {
        "\n\n${it.name} ${it.score}"
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