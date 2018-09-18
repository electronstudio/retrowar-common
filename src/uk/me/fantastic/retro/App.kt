// <editor-fold desc="Copyright 2018 Richard Smith">
/*
    Copyright 2018 Richard Smith.

    RetroWar is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    RetroWar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with RetroWar.  If not, see <http://www.gnu.org/licenses/>.
*/
// </editor-fold>
package uk.me.fantastic.retro

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.controllers.Controllers
import com.codedisaster.steamworks.SteamAPI
import de.golfgl.gdxgameanalytics.GameAnalytics
import uk.me.fantastic.retro.Prefs.BinPref
import uk.me.fantastic.retro.input.GamepadInput
import uk.me.fantastic.retro.input.KeyboardMouseInput
import uk.me.fantastic.retro.input.MappedController
import uk.me.fantastic.retro.input.SimpleTouchscreenInput
import uk.me.fantastic.retro.input.StatefulController
import uk.me.fantastic.retro.music.ibxm.IBXMPlayer
import uk.me.fantastic.retro.network.Client
import uk.me.fantastic.retro.network.Server
import uk.me.fantastic.retro.screens.GameSession
import uk.me.fantastic.retro.utils.RetroShader
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import kotlin.concurrent.thread

/**
 * Main libgdx common application class.  Created by platform specific launchers.
 * Delegates actual rendering loop to Screens
 *
 * For most games you can use subclass SimpleApp rather than App directly.
 *
 * @param callback For setting maximum FPS, platform specific
 * @param logger If debug is on then logs are sent here, which may send them to screen, file or
 * cloud.
 * @param manualGC GDX on iOS has very poor garbage collection.  Supply one of these to disable
 * it and do your own GC.  Otherwise null.
 */
abstract class App(val callback: Callback, val logger: Logger, val manualGC: ManualGC? = null) :
        Game() {

    /** Title screen */
    var title: Screen? = null

    /** If you are using GameAnalytics service set this, otherwise null */
    var gameAnalytics: GameAnalytics? = null

    companion object {
        /** A static reference to the singleton Application */
        @JvmStatic
        lateinit var app: App

        /** Where log file is stored */
        val LOG_FILE_PATH: String = System.getProperty("user.home") + File.separator + "retrowar-log.txt"
        /** Where preferences file is stored */
        val PREF_DIR: String = System.getProperty("user.home") + File.separator + ".prefs"
    }

    init {
        app = this
        findIPaddress()
    }

    /** Tries to connect to AWS to get current IP address
     *  in separate thread so wont slow down launch waiting for result
     */
    protected fun findIPaddress() {
        thread {
            try {
                val whatismyip = URL("http://checkip.amazonaws.com")
                val i = BufferedReader(InputStreamReader(whatismyip.openStream()))
                ip = i.readLine()
            } catch (e: Exception) {
            }
        }
    }

    /** Uses the Callback to set max FPS, if the platform supports it */
    fun setFPS(f: Int) {
        callback.setForegroundFPS(f)
        callback.setBackgroundFPS(f)
    }

    /** Setup network stuff, not currently working */
    protected fun initialiseNetwork() {
        server = Server()
        server?.initialise()
        client = Client()
        client?.initialise()
    }

    /** All the controllers currently connected */
    internal val mappedControllers = ArrayList<MappedController>()

    /** All controllers each wrapped in StatefulController objects, useful for menu input */
    internal val statefulControllers = ArrayList<StatefulController>()

    /** Current IP address, if known.  Else "unknown" */
    var ip: String = "unknown"

    /** May be null if no Server */
    var server: Server? = null

    /** May be null if no Client */
    var client: Client? = null

    lateinit var controllerTest: GameFactory
    lateinit var screenTest1: GameFactory
    lateinit var screenTest2: GameFactory

    val ibxmPlayer = IBXMPlayer()

    internal var mouseClicked = false

    lateinit var shader: RetroShader

    /** has any key or controller button or mouse recently been hit? */
    fun anyKeyHit(): Boolean {
        return statefulControllers.any { it.isButtonAJustPressed } ||
                app.mouseJustClicked ||
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
    }

    /** For testing sandbox permissions, attempts to read property we shouldnt be allowed to read */
    fun testSandbox(): String {
        return (App::class.java.name + ": I shouldnt be able to do this: " + System.getProperty("os" +
                ".name"))
    }

    val mouseJustClicked: Boolean
        get() {
            val t = mouseClicked
            mouseClicked = false
            return t
        }

    val versionString = (App::class.java.`package`.implementationVersion ?: "devel")

    /** Displays a new screen and disposes of the old one */
    fun swapScreenAndDispose(screen: Screen) {
        log("swapscreen")
        val s = app.screen
        app.setScreen(screen)
        s.dispose()
    }

    fun showTitleScreen() {
        val title = title
        if (title != null) {
            swapScreenAndDispose(title)
        } else {
            log("There is no titlescreen so exiting")
            quit()
        }
    }

    abstract fun quit()

    /** If player hit a key during game, you dont want that event to then take effect in the menu after the game,
    * so you call this.
     */
    fun clearEvents() {
        statefulControllers.forEach { it.clearEvents() }
        mouseJustClicked // eat the event if there is a click already waiting
    }

    /** Prefs can have behviour attached, like setting the screen resolution.
     * This triggers it for all of them, if they have it.
     */
    protected fun initialisePrefs() {
        BinPref.values().forEach(BinPref::apply)
        Prefs.MultiChoicePref.LIMIT_FPS.apply()
    }

    /**
     * Populates mappedControllers
     */
    protected fun initialiseControllers() {
        println("Detected ${Controllers.getControllers().size} controllers")

        Controllers.getControllers().mapTo(mappedControllers, ::MappedController)

        mappedControllers.mapTo(statefulControllers, ::StatefulController)
    }

    protected fun initializeInput() {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
                log("app touchdown")
                mouseClicked = true
                return true
            }
        }
    }

    protected fun initialiseDesktop() {
    }

    protected fun initialiseAndroid() {
        if (Gdx.app.type == Application.ApplicationType.Android) {
            Gdx.input.isCatchBackKey = true
        }
    }

    fun initialiseShader() {
        shader = RetroShader("shaders/" + Prefs.MultiChoicePref.SHADER.getString())
    }

    protected fun initialiseSteam() {
        System.out.println("Initialise Steam client API ...")

        if (!SteamAPI.init()) {
            log("steam error")
            SteamAPI.printDebugInfo(System.err)
        }

        SteamAPI.printDebugInfo(System.out)
    }

    fun setScreenMode() {
        if (BinPref.FULLSCREEN.isEnabled()) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
        } else {
            Gdx.graphics.setWindowedMode(832, 512)
        }
        Gdx.graphics.setVSync(BinPref.VSYNC.isEnabled())
    }

    open fun submitAnalytics(s: String) {
        gameAnalytics?.submitDesignEvent(s)
        gameAnalytics?.flushQueueImmediately()
    }

    /**
     * For some games, e.g. a standalone mobile game, you dont want the player to have
     * to hit a button to enter the game.  This auto-adds the first player to the game
     * so he doesnt have to this.
     */
    fun configureSessionWithPreSelectedInputDevice(session: GameSession){
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            val controller1 = App.app.mappedControllers.firstOrNull()
            if (controller1 != null) {
                session.preSelectedInputDevice = GamepadInput(controller1)
            } else {
                session.preSelectedInputDevice = KeyboardMouseInput(session)
                session.KBinUse = true
            }
        } else if (isMobile) {
            session.preSelectedInputDevice = SimpleTouchscreenInput()
        }
    }
}

// class SingleGameAppFromClass(callback: Callback, val name: String, val gameClazz: Class<out uk.me.fantastic.retro.Game>, val screenClazz: Class<out Screen>, val t: Screen? = null) : App(callback) {
//
//    val factory: AbstractGameFactory =
//            GameFactory(name = name,
//                    createGame =
//                    { session: GameSession -> (gameClazz.getConstructor(GameSession::class.java).newInstance(session)) }
//            )
//
//
//    override fun create() {
//        log("SingleGameAppFromClass create")
//
//        app = this
//        games = listOf(factory)
//
//        initialiseAndroid()
//        initialiseDesktop()
//        setPrefsToDefaultsForSingleGames()
//        initialisePrefs()
//        initializeInput()
//        initialiseControllers()
//
//
//        val game = GameSession(factory)
//
//        if (screenClazz != null) {
//            title = screenClazz.newInstance()
//            setScreen(title)
//        } else {
//           setScreen(game)
//        }
//
//    }
// }
