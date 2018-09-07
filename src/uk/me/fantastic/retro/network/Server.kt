package uk.me.fantastic.retro.network

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import org.objenesis.strategy.StdInstantiatorStrategy
import uk.me.fantastic.retro.log
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CopyOnWriteArrayList

class Server : Listener() {

    val BUFFER_SIZE = 1024 * 1024 * 10
    val server = Server(BUFFER_SIZE, BUFFER_SIZE)

    val queue = ArrayBlockingQueue<Pair<Connection, Any>>(10)

    // private var connectionQueue = ArrayBlockingQueue<Connection>(64)
    // private var disConnectionQueue = ArrayBlockingQueue<Connection>(64)

    private var clients = CopyOnWriteArrayList<Connection>()

    fun initialise() {
        // registerKryo(server.kryo)
        server.kryo.isRegistrationRequired = false
        server.kryo.instantiatorStrategy = StdInstantiatorStrategy()

        try {
            server.bind(54555, 54777)
            server.addListener(this)

            server.start()
            log("server running")
        } catch (e: java.net.BindException) {
            log("couldnt start a server, port in use")
        }
    }

    override fun received(connection: Connection, obj: Any) {
        queue.offer(Pair(connection, obj))
    }

    override fun connected(connection: Connection) {
        clients.add(connection)
        log("Server: Connected client $connection")
    }

    override fun disconnected(connection: Connection) {
        clients.remove(connection)
        log("Server: Disconnected client $connection")
    }

//    fun startGame(gf: AbstractGameFactory){
//        sendReliable(gf)
//    }

    fun send(obj: Any) {
        log("server send $obj")
        server.sendToAllUDP(obj)
//        clients.forEach {
//            log("  to $it")
//            it.sendUDP(obj)
//        }
    }

    fun sendReliable(obj: Any) {
        log("sendreliable")
        server.sendToAllTCP(obj)
//        clients.forEach {
//            log("sending $it $obj")
//            it.sendTCP(obj)
//        }
    }

    companion object {
//        internal fun registerKryo(kryo: Kryo) {
//            kryo.register(JoinRequest::class.java)
//            kryo.register(WorldUpdate::class.java)
//            kryo.register(InputUpdate::class.java)
//            kryo.register(CreatePlayerRequest::class.java)
//            kryo.register(CreatePlayerResponse::class.java)
//            kryo.register(NetworkInput::class.java)
//            kryo.register(Vec::class.java)
//
//            kryo.register(ByteArray::class.java)
//            kryo.register(Text::class.java)
//            kryo.register(Player::class.java)
//            kryo.register(ClientPlayer::class.java)
//            kryo.register(PlayersUpdate::class.java)
//            kryo.register(ArrayList::class.java)
//            kryo.register(com.badlogic.gdx.graphics.Color::class.java)
//            kryo.register(Pair::class.java)
//
//            kryo.isRegistrationRequired = true
//        }
    }
}
