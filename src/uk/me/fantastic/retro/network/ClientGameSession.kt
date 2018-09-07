package uk.me.fantastic.retro.network

import uk.me.fantastic.retro.AbstractGameFactory
import uk.me.fantastic.retro.App.Companion.app
import uk.me.fantastic.retro.Player
import uk.me.fantastic.retro.input.NetworkInput
import uk.me.fantastic.retro.log
import uk.me.fantastic.retro.screens.GameSession

class ClientGameSession(factory: AbstractGameFactory) : GameSession(factory) {

    override fun show() {
        log("Gamesession $game show input $preSelectedInputDevice")
        // val game = game

        if (game == null) {
            throw Exception("Trying to show a GameSession but its Game has not been set yet")
        }
        game!!.show()
    }

    override fun render(deltaTime: Float) {
        val networkedGame = game
        val client = app.client
        if (networkedGame is isNetworked && client != null) {

            var obj: Any? = app.client?.queue?.poll()

            while (obj != null) {
                when (obj) {
                    is SessionUpdate -> {
                        log("is SessionUpdate ${obj.players} ${obj.players.size}")
                        players = ArrayList<Player>()
                        players.addAll(obj.players)
                    }
                    else -> {
                        log("passing it on")
                        networkedGame.processIncomingMessage(obj)
                    }
                }
                obj = client.queue.poll()
            }

            client.player?.let {
                val n = NetworkInput()
                n.copyFrom(it.input)
                client.client.sendUDP(n)
            }
        }
        super.render(deltaTime)
    }

    class SessionUpdate(val players: Array<Player>)
}