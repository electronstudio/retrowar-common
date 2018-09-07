package uk.me.fantastic.retro.network

import com.badlogic.gdx.graphics.Color
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import org.objenesis.strategy.StdInstantiatorStrategy
import uk.me.fantastic.retro.AbstractGameFactory
import uk.me.fantastic.retro.App.Companion.app
import uk.me.fantastic.retro.Player
import uk.me.fantastic.retro.Prefs
import uk.me.fantastic.retro.input.GamepadInput
import uk.me.fantastic.retro.log
import java.util.concurrent.ArrayBlockingQueue

class Client : Listener() {
    val BUFFER_SIZE = 1024 * 1024 * 10

    val client = Client(BUFFER_SIZE, BUFFER_SIZE)
    val queue = ArrayBlockingQueue<Any>(10)

    var gameToLoad: AbstractGameFactory? = null

    var player: Player? = null

    fun initialise() {
        log("initialize networkclient")

        // Server.registerKryo(client.kryo)
    }

    fun connect() {
        val p = Player(
                GamepadInput(app.mappedControllers.first()),
                Prefs.StringPref.PLAYER1.getString(),
                Color.valueOf(Prefs.MultiChoicePref.PLAYER1_COLOR.getString()),
                Color.valueOf(Prefs.MultiChoicePref.PLAYER1_COLOR2.getString())
        )
        connect(p)
    }

    fun connect(p: Player) {
        player = p
        client.kryo.isRegistrationRequired = false
        client.kryo.instantiatorStrategy = StdInstantiatorStrategy()

        client.addListener(this)

        client.start()
        log("networkclient started")
        client.connect(5000, Prefs.StringPref.SERVER.getString(), 54555, 54777)
    }

    override fun connected(connection: Connection) {
    }

    override fun received(connection: Connection, obj: Any) {
        when (obj) {
            is AbstractGameFactory -> {
                gameToLoad = obj
                player?.let {
                    client.sendTCP(it)
                }
            }
            else -> {
                queue.offer(obj)
            }
        }
    }
}