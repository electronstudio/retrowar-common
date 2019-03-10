package uk.co.electronstudio.retrowar.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.utils.Align
import uk.co.electronstudio.retrowar.AbstractGameFactory
import uk.co.electronstudio.retrowar.App
import uk.co.electronstudio.retrowar.App.Companion.app
import uk.co.electronstudio.retrowar.FBORenderer
import uk.co.electronstudio.retrowar.Prefs
import uk.co.electronstudio.retrowar.Prefs.BinPref
import uk.co.electronstudio.retrowar.Resources
import uk.co.electronstudio.retrowar.log
import uk.co.electronstudio.retrowar.menu.ActionMenuItem
import uk.co.electronstudio.retrowar.menu.BackMenuItem
import uk.co.electronstudio.retrowar.menu.BinPrefMenuItem
import uk.co.electronstudio.retrowar.menu.Menu
import uk.co.electronstudio.retrowar.menu.MenuController
import uk.co.electronstudio.retrowar.menu.MultiPrefMenuItem
import uk.co.electronstudio.retrowar.menu.NumPrefMenuItem
import uk.co.electronstudio.retrowar.menu.SubMenuItem

/**
 * Created by richard on 23/06/2016.
 * GDX screen, i.e. a render loop, used to render the titlescreen
 */
open class SimpleTitleScreen(
    val WIDTH: Float = 160f,
    val HEIGHT: Float = 120f,
    val FONT: BitmapFont = Resources.FONT, // BitmapFont(Gdx.files.internal("small.fnt")),
    val title: String = "My Game",
    val factory: AbstractGameFactory,
    val quitText: String = "Quit",
    val quitURL: String? = null
) : ScreenAdapter() {

    val FONT_ENGLISH = FONT

    val renderer = FBORenderer(WIDTH, HEIGHT, false)

    val quitMenu = Menu("Quit?")

    val videoOptions = Menu("")

    val titleMenu: Menu = Menu("", quitAction = {
        if (quitURL != null) {
            Gdx.net.openURI(quitURL)
        }
        app.quit()
    })

    val optionsMenu: Menu = Menu("")

    val soundOptions: Menu = Menu("")

    internal var glyphLayout = GlyphLayout()

    val controller = MenuController(titleMenu, WIDTH, HEIGHT, y = 100f)

    var timer: Float = 0f
    var count = 0

    val sequence = arrayOf("RED", "PURPLE", "BLUE", "CYAN", "GREEN", "YELLOW")

    var flash = ""

    val header = "\n" + title + "\n"

    val footer = "V" + app.versionString

    /* not sure if this is OK or whether it should all be re-initialized in show() */
    init {

        FONT.data.markupEnabled = true

        FONT.region.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)

        videoOptions.addAll(listOf(MultiPrefMenuItem("Graphics ", Prefs.MultiChoicePref.GRAPHICS),
            BinPrefMenuItem("Scaling ", BinPref.STRETCH),
            BackMenuItem("BACK")))

        soundOptions.addAll(listOf(BinPrefMenuItem("Game Music ", BinPref.MUSIC),
            NumPrefMenuItem("Music Volume ", numPref = Prefs.NumPref.MUSIC_VOLUME),
            NumPrefMenuItem("FX Volume ", numPref = Prefs.NumPref.FX_VOLUME),
            BackMenuItem("BACK")))

        titleMenu.addAll(listOf(
            //   SubMenuItem("Start game", optionsMenu),
            ActionMenuItem("Start Game", {

                App.app.screen = GameSession(factory)
            }),

            SubMenuItem("Options", subMenu = optionsMenu)))

        // items.add(MenuItem("Connect to server"))
        optionsMenu.add(SubMenuItem("Video", subMenu = videoOptions))

        optionsMenu.add(SubMenuItem("Sound", subMenu = soundOptions))

        optionsMenu.add(BackMenuItem("BACK"))

        titleMenu.add(BackMenuItem(quitText))

        quitMenu.add(BackMenuItem("No"))

        quitMenu.add(ActionMenuItem("Yes", action = {
            if (quitURL != null) {
                Gdx.net.openURI(quitURL)
            }
            app.quit()
        }))
    }

    override fun show() {
        log("show titlescreen")
        timer = 0f
        app.clearEvents()
    }

    override fun render(delta: Float) {
        timer += delta

        val mouse = renderer.convertScreenToGameCoords(Gdx.input.x, Gdx.input.y)

        controller.doMouseInput(mouse.x, mouse.y)
        if (timer > 0.2f) {
            controller.doInput()
        } else {
            app.clearEvents()
        }
        renderToFBO(renderer.beginFBO())

        renderer.renderFBOtoScreen()
    }

    private fun renderToFBO(batch: Batch) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f) // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()

        flash = sequence[count++ % sequence.size]

        // fixme encapsulate menu inside controler
        val text = header + controller.menus.peek().getText(flash)

        glyphLayout.setText(FONT, text, Color.WHITE, WIDTH, Align.center, true)

        //    Prefs.BinPref.BILINEAR.filter(FONT.region)

        FONT.draw(batch, glyphLayout, 0f, HEIGHT)
        FONT_ENGLISH.draw(batch, footer, 0f, 255f)

        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        log("TitleScreen resize $width $height")
        renderer.resize(width, height)
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
        // App.stopMusic()
    }

    override fun dispose() {
    }
}
