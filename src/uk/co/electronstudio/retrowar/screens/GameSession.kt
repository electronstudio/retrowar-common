package uk.co.electronstudio.retrowar.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.Controllers

import com.badlogic.gdx.graphics.Color
import com.esotericsoftware.kryonet.Connection
import uk.co.electronstudio.mobcontrol.MobController
import uk.co.electronstudio.retrowar.*
import uk.co.electronstudio.retrowar.App.Companion.app
import uk.co.electronstudio.retrowar.input.GamepadInput
import uk.co.electronstudio.retrowar.input.InputDevice
import uk.co.electronstudio.retrowar.input.KeyboardMouseInput
import uk.co.electronstudio.retrowar.input.NetworkInput
import uk.co.electronstudio.retrowar.menu.ActionMenuItem
import uk.co.electronstudio.retrowar.menu.BackMenuItem
import uk.co.electronstudio.retrowar.menu.BinPrefMenuItem
import uk.co.electronstudio.retrowar.menu.Menu
import uk.co.electronstudio.retrowar.menu.MultiPrefMenuItem
import uk.co.electronstudio.retrowar.menu.NumPrefMenuItem
import uk.co.electronstudio.retrowar.menu.SubMenuItem
import uk.co.electronstudio.retrowar.network.ClientGameSession
import uk.co.electronstudio.retrowar.network.ClientPlayer
import uk.co.electronstudio.sdl2gdx.RumbleController

import java.util.ArrayList

/**
 * Created by richard on 23/06/2016.
 * A GDX screen (i.e. a render loop, used whenever a game is in progress)
 * intended to provide continuity between multiple games.
 * If you're only playing a single game this could be part of the game class,
 * but for a series of games you pass a single GameSession from game to game
 * to maintain the same players, scores, (network connections?)
 */
open class GameSession(
        val factory: AbstractGameFactory,
        val useSimpleGameSettings: Boolean = false
        // val preSelectedInputDevice: InputDevice? = null
) : ScreenAdapter() {

    var game: Game? = null
    var preSelectedInputDevice: InputDevice? = null

    var level = 0

    var nextGame: Game? = null // allows chaining several games together
    var metaGame: Game? = null // allows one game to launch minigames that return control to the parent game when done

    var readyTimer = 0f

    enum class GameState {
        PLAY, GAMEOVER, MENU, GETREADY
    }

    var state = GameState.GETREADY // fixme only used by UniGame, does it need to be in session?

    var players = ArrayList<Player>()
    val clientPlayers = ArrayList<ClientPlayer>() // when we a network client this is the players on the local machine
    val connections: ArrayList<Connection> = ArrayList()

    var buffering = true

    var KBinUse = false

    val server: String = Prefs.StringPref.SERVER.getString()

    enum class NetworkRole { NONE, CLIENT, SERVER }

    internal val usedControllers = ArrayList<Controller>()

    private val remotePlayers = HashMap<Int, Player>()
    private val networkInputs = HashMap<Int, NetworkInput>()

    private var namesUsed = 0 // FIXME use this

    var disposed = false

    override fun dispose() {
        disposed = true
        super.dispose()
        game?.dispose()
    }

    var playersJoined = 0

    init {

        //  if(client) setNetworkRoleToClient()

        //        if (minigame == null) {
        //           // game = ButtonMasherGame3(this)
        //            game = UniGame(CharacterFactory() , session=this, networkRole = networkRole)
        //        } else {
        //            game = minigame
        //        }

        // val clazz = UniGame::class
        // game = clazz.primaryConstructor?.call()
        resetTimer()
        game = if (useSimpleGameSettings) factory.createWithSimpleSettings(this) else factory.create(this)
        val level = factory.levels?.get(factory.level)
        app.submitAnalytics("sessionStart:${factory.name}")
        level?.let {
            app.submitAnalyticsProgress(factory.name, it.name)
        }
    }

    private fun resetTimer() {
        readyTimer = if (Prefs.BinPref.DEBUG.isEnabled()) 1f else 40f
        state = GameSession.GameState.GETREADY
    }

    fun createClient(connection: Connection) {
        connections.add(connection)
        log("created connection " + connection)
        postMessage("${connection.remoteAddressTCP} connected")
    }

    fun removeClient(client: Connection) {

        log("players: ${players.size}")

        players.removeIf {
            if (it.input is NetworkInput) {
                log("removeclient 1 $it ${it.input.clientId} ${client.id}")
                if (it.input.clientId == client.id) {
                    log("removing: $it")
                    return@removeIf true
                }
            }
            return@removeIf false
        }
        log("players: ${players.size}")

        connections.remove(client)
        postMessage("${client.id} disconnected")
    }


    fun createKBPlayer() {
        if (KBinUse && !Prefs.BinPref.DEBUG.isEnabled()) return
        KBinUse = true

        val i = KeyboardMouseInput(this)
        createPlayer(i, null) // FIXME keyboard player never has any name data

        //  val ship = createCharacter(i, player)
    }

    private fun createParsecPlayer(controller: NetworkController, playerData: PlayerData?): Player {
        val player = createControllerPlayer(controller, playerData)
        controller.player = player
        return player
    }

    private fun createControllerPlayer(controller: RumbleController, playerData: PlayerData?): Player {

//        val c = controller.javaClass
//        val m = c.methods.find { it.name.equals("rumble") }
//        m!!.invoke(controller, 1f, 1f, 500)

        // if (controller is RumbleController) {
        controller.rumble(0.0f, 0.5f, 5000)
        // }
        val gamepad = GamepadInput(controller)
        return createPlayer(gamepad, playerData)
    }

    //    var touchscreenInput: TouchscreenInput? = null
    //
    //    fun createTouchscreenPlayer() {
    //        if (touchscreenInput == null) {
    //            val t = TouchscreenInput()
    //            touchscreenInput = t
    //            createPlayer(t)
    //        }
    //    }

    @SuppressWarnings
    fun standardMenu(): Menu {

        val controller1 = Menu("Show controls - Controller", image = Resources.CONTROLLER2)
        controller1.add(BackMenuItem("<<<<"))

        val controller2 = Menu("Show controls - Dpad", image = Resources.CONTROLLER1)
        controller2.add(BackMenuItem("<<<<"))

        val controller3 = Menu("Show controls - Keyboard", image = Resources.CONTROLLER3)
        controller3.add(BackMenuItem("<<<<"))

        val inGameMenu: Menu = Menu("MENU", quitAction = {
            requestStateChangeMenuToPlay()
        })
        val inGameVideoOptions = Menu("VIDEO")

        val inGameViewControls = Menu("VIEW CONTROLS")


        // inGameOptions.add(BinPrefMenuItem("Display mode: ", Prefs.BinPref.FULLSCREEN))
        inGameVideoOptions.add(MultiPrefMenuItem("Shader: ", Prefs.MultiChoicePref.SHADER))
        inGameVideoOptions.add(BinPrefMenuItem("Vsync: ", Prefs.BinPref.VSYNC))
        inGameVideoOptions.add(MultiPrefMenuItem("FPS limit: ", Prefs.MultiChoicePref.LIMIT_FPS))
        inGameVideoOptions.add(BinPrefMenuItem("Pixels: ", Prefs.BinPref.SMOOTH))
        inGameVideoOptions.add(BinPrefMenuItem("Blur: ", Prefs.BinPref.BILINEAR))
        inGameVideoOptions.add(BinPrefMenuItem("Scaling: ", Prefs.BinPref.STRETCH))
        inGameVideoOptions.add(BinPrefMenuItem("Scanlines: ", Prefs.BinPref.SCANLINES))
        inGameVideoOptions.add(BinPrefMenuItem("Show FPS: ", Prefs.BinPref.FPS))
        inGameVideoOptions.add(BackMenuItem("<<<<"))

        inGameMenu.add(SubMenuItem("Video Options", subMenu = inGameVideoOptions))

        val inGameControlOptions = Menu("CONTROLS")
        inGameControlOptions.add(BinPrefMenuItem("Control Type: ", Prefs.BinPref.ANALOG_CONTOLRS))
        inGameControlOptions.add(NumPrefMenuItem("Deadzone % ", Prefs.NumPref.DEADZONE))
        inGameControlOptions.add(MultiPrefMenuItem("Rumble: ", Prefs.MultiChoicePref.RUMBLE))
        inGameControlOptions.add(BackMenuItem("<<<<"))

        inGameMenu.add(SubMenuItem("Controller Options", subMenu = inGameControlOptions))

        inGameMenu.add(SubMenuItem("View controls", subMenu = inGameViewControls))

        inGameViewControls.add(SubMenuItem("Controller", subMenu = controller1))
        inGameViewControls.add(SubMenuItem("DPAD", subMenu = controller2))
        inGameViewControls.add(SubMenuItem("Keyboard & Mouse", subMenu = controller3))
        inGameViewControls.add(BackMenuItem("<<<<"))

        val quitMenu = Menu("QUIT?")
        quitMenu.add(BackMenuItem("No"))
        quitMenu.add(ActionMenuItem("YES", action = {
            this.nextGame = null
            this.metaGame = null
            this.quit()
        }))

        inGameMenu.add(SubMenuItem("Quit", subMenu = quitMenu))

        return inGameMenu
    }

    private var requestStateChangeMenuToPlay = false

    private fun requestStateChangeMenuToPlay() {
        requestStateChangeMenuToPlay = true
    }

    private fun createPlayer(input: InputDevice, playerData: PlayerData?): Player {

        return createLocalPlayerOnServer(input, playerData)
    }

    fun createNetworkPlayerOnServer(name: String): Int {

        val id = players.size

        log("create network player $id")

        val player = Player(input = NetworkInput(),
                name = name,
                color = Color.valueOf((nextPlayerColor())),
                color2 = Color.valueOf(nextPlayerColor2()))

        players.add(player)
        playersJoined++

        postMessage("${player.name} JOINED!")

        return id
    }

    private fun createLocalPlayerOnServer(input: InputDevice, playerData: PlayerData?): Player {

        val id = players.size

        log("create player $id $input ${playerData?.name} ${playerData?.color}")

        val namePref = nextPlayerName()

        val player =
                if (playerData == null) {
                    Player(input = input,
                            name = namePref,
                            color = Color.valueOf((nextPlayerColor())),
                            color2 = Color.valueOf(nextPlayerColor2()))
                } else {
                    Player(input, playerData.name, playerData.color, playerData.color2)
                }

        players.add(player)
        playersJoined++
        input.player = player

        log("added player, players now ${players.size}")

        postMessage("${player.name} JOINED!")


        return player
    }

    private fun createPlayerOnClient(input: InputDevice): Int {
        val id = clientPlayers.size

        log("create local player $id")

        val namePref = when (id) {
//            0 -> Prefs.StringPref.PLAYER1.getString()
//            1 -> Prefs.StringPref.PLAYER2.getString()
//            2 -> Prefs.StringPref.PLAYER3.getString()
//            3 -> Prefs.StringPref.PLAYER4.getString()
            else -> Prefs.StringPref.PLAYER_MORE.getString() + (id + 1)
        }
        val player = ClientPlayer(input = input,
                name = namePref,
                localId = id,
                color = Color.valueOf((nextPlayerColor())),
                color2 = Color.valueOf(nextPlayerColor2()))
        clientPlayers.add(player)

        // postMessage("${player.name} JOINED!")
        return id
    }

    override fun show() {
        log("Gamesession $game show input $preSelectedInputDevice")
        // val game = game

        if (game == null) {
            throw Exception("Trying to show a GameSession but its Game has not been set yet")
        }
        game!!.show()
        app.server?.sendReliable(factory)

//        for (c in app.mappedControllers) {
//            log("attaching listener to $c")
//            attachListenerToController(c)
//        }
        preSelectedInputDevice?.let {
            createPlayer(it, null)
        }

        app.controllerMappings.forEach { (controller, playerData) ->
            createControllerPlayer(controller, playerData)
            usedControllers.add(controller)
        }


        //            if (Gdx.app.type == Application.ApplicationType.Android) {
        //                createTouchscreenPlayer()
        //            }
    }

//    private fun attachListenerToController(c: MappedController) {
//        preSelectedInputDevice?.let {
//            if (it is GamepadInput && it.controller == c) {
//                return // dont use controller if its already been used as the preselectedinputdevice
//            }
//        }
//
//        c.listener = object : ControllerAdapter() {
//            override fun buttonDown(controller: Controller?, buttonIndex: Int): Boolean {
//
//                log("controller $controller $buttonIndex")
//
//                if (!usedControllers.contains(c)) {
//                    createControllerPlayer(c)
//                    usedControllers.add(c)
//
//                    //  unusedControllers.removeValue(controller, true)
//                }
//                return true
//            }
//        }
//        c.controller.addListener(c.listener)
//    }

    fun checkForPlayerJoins() {
        val controllers = Controllers.getControllers() as com.badlogic.gdx.utils.Array<RumbleController>
        controllers.forEach {
            val p = preSelectedInputDevice
            if (p == null || p !is GamepadInput || p.controller != it) {
                for (i in 0..15) {
                    if (it.getButton(i)) {
                        if (!usedControllers.contains(it)) {
                            createControllerPlayer(it, null)
                            usedControllers.add(it)
                        }
                    }
                }
            }
        }

        val mobileControllers = app.mobControllerManager.controllers as com.badlogic.gdx.utils.Array<MobController>
        mobileControllers.forEach{
            if (!usedControllers.contains(it)) {
                val data = PlayerData(it.playerName, Color(it.colour1), Color(it.colour2))
                createControllerPlayer(it, data)
                usedControllers.add(it)
            }
        }
    }

    private fun checkForParsecJoins() {
        app.networkControllers.values.forEach {
            if (!usedControllers.contains(it)) {
                val playerData = PlayerData(it.guestName, color = Color.valueOf(nextPlayerColor()), color2 = Color.valueOf(nextPlayerColor2()))
                val player = createParsecPlayer(it, playerData)
                usedControllers.add(it)
            }
        }
    }


    fun checkForPlayerDisconnects() {
        val removals = players.filter { it.input is GamepadInput && !app.getAllControllersIncludingParsec().contains(it.input.controller) }
        for (player in removals) {
            players.remove(player)
            postMessage("${player.name} disconnected")
        }
        //players.removeAll { it.input is GamepadInput && !Controllers.getControllers().contains(it.input.controller) }
    }

    override fun resize(width: Int, height: Int) {
        game?.resize(width, height)
    }

    override fun render(deltaTime: Float) {
        if (disposed) return
        if (requestQuit) {
            reallyQuit()
            return
        }
        checkForPlayerJoins()
        checkForParsecJoins()
        checkForPlayerDisconnects()

        val message = app.parsec?.pollMessages()

        if(message!=null) postMessage(message)

        game?.renderAndClampFramerate()

        if (state == GameSession.GameState.GETREADY) {
            readyTimer -= deltaTime * 10
            if (readyTimer < 0f) {
                state = GameSession.GameState.PLAY
            }
        }
        if (state == GameSession.GameState.PLAY || state == GameSession.GameState.GETREADY) {
            if (input.isKeyJustPressed(Input.Keys.BACK)) {
                app.showTitleScreen()
            }
            if (input.isKeyJustPressed(Input.Keys.ESCAPE) || app.statefulControllers.any { it.isAnyLittleButtonJustPressed }) {
                log("GameSession", "in play, menu")
                state = GameSession.GameState.MENU
                app.clearEvents()
            }
            if (input.isKeyJustPressed(Input.Keys.SPACE)) {
                createKBPlayer()
            }
            if (Prefs.BinPref.DEBUG.isEnabled() && input.isKeyJustPressed(Input.Keys.N)) {
                if (players.isNotEmpty()) players[0].score += 10
            }
        }

        if (requestStateChangeMenuToPlay) {
            state = GameSession.GameState.PLAY
            requestStateChangeMenuToPlay = false
        }

//        else if(state==GameState.MENU){
//
//            if (input.isKeyJustPressed(Input.Keys.ESCAPE) || app.statefulControllers.any { it.isAnyLittleButtonJustPressed }) {
//                log("GameSession","in menu, back")
//                this.state = GameState.PLAY
//                app.clearEvents()
//            }
//        }

        val server = app.server

        var pair = server?.queue?.poll()

        while (pair != null) {
            var (connection, obj) = pair
            when (obj) {
                is Player -> {
                    val input = NetworkInput(clientId = connection.id)
                    val p = Player(input, obj.name, obj.color, obj.color2)
                    players.add(p)
                    playersJoined++
                    remotePlayers.put(connection.id, p)
                    networkInputs.put(connection.id, input)
                }
                is InputDevice -> {
                    val i = networkInputs[connection.id]
                    if (i != null) {
                        i.copyFrom(obj)
                    }
                }
                else -> {
                    log("unknown message received")
                }
            }
            pair = server?.queue?.poll()
        }

        val connections = app.server?.server?.connections?.size

        if (connections != null && connections > 0 && players.size > 0) {
            log("sending sessionupdate")
            val p: Array<Player> = Array(players.size, { players[it] })
            val update = ClientGameSession.SessionUpdate(p)
            app.server?.send(update)
        }
    }


    override fun hide() {
        game?.hide()

        Gdx.input.isCursorCatched = false

//        app.mappedControllers.filter { it.listener != null }.forEach {
//            it.controller.removeListener(it.listener)
//        }
    }

    // FIXME pre-create array of 16 players and pop the top one when player needed

    fun nextPlayerName(): String {
        return Prefs.StringPref.PLAYER_MORE.getString() + (playersJoined + 1)
    }

    fun nextPlayerColor(): String {
        return when (playersJoined) {
            0 -> Prefs.MultiChoicePref.PLAYER1_COLOR.getString()
            1 -> Prefs.MultiChoicePref.PLAYER2_COLOR.getString()
            2 -> Prefs.MultiChoicePref.PLAYER3_COLOR.getString()
            3 -> Prefs.MultiChoicePref.PLAYER4_COLOR.getString()
            4 -> Prefs.MultiChoicePref.PLAYERGUEST_COLOR.getString()
            else -> Resources.palette.random().toString()
        }
    }

    fun nextPlayerColor2(): String {
        return when (playersJoined) {
            0 -> Prefs.MultiChoicePref.PLAYER1_COLOR2.getString()
            1 -> Prefs.MultiChoicePref.PLAYER2_COLOR2.getString()
            2 -> Prefs.MultiChoicePref.PLAYER3_COLOR2.getString()
            3 -> Prefs.MultiChoicePref.PLAYER4_COLOR2.getString()
            4 -> Prefs.MultiChoicePref.PLAYERGUEST_COLOR2.getString()
            else -> Resources.palette.random().toString()
        }
    }

    fun postMessage(s: String) {
        game?.postMessage(s)
    }

    var requestQuit = false

    fun quit() {
        requestQuit = true
    }

    fun reallyQuit() {
        requestQuit = false
        log("gamesession quit")
        App.app.steam?.incGamesPlayed()
        nextGame?.let {
            advanceToNextGame(it)
        } ?: metaGame?.let {
            advanceToNextGame(it)
        } ?: app.restoreSavedScreenAndDisposeCurrentScreen()
    }

    private fun advanceToNextGame(gameToShow: Game) {
        game?.hide()
        game?.dispose()
        resetTimer()
        game = gameToShow
        game?.show()
    }

    // search for Player (or multiple Players) with highest score
    fun findWinners(): List<Player> {
        var highScore = Integer.MIN_VALUE
        val winners = ArrayList<Player>()
        for (player in players) {
            if (player.score == highScore) {
                winners.add(player)
            } else if (player.score > highScore) {
                highScore = player.score
                winners.clear()
                winners.add(player)
            }
        }
        return winners
    }

    fun startSubgameInMetagame(metagame: Game, game: Game) {
        players.forEach { it.reset() }
        this.metaGame = metagame
        state = GameSession.GameState.GETREADY
        this.game = game
        resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    fun metaGameOver() {
        nextGame = null
        metaGame = null
        quit()
    }

    //    private fun setNetworkRoleToClient() {
    //        game.setNetworkRoleToClient()
    //    }
}
