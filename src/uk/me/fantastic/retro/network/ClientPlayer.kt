package uk.me.fantastic.retro.network

import com.badlogic.gdx.graphics.Color
import uk.me.fantastic.retro.Player
import uk.me.fantastic.retro.input.InputDevice

/**
 * specialized kind of Player used by networking TODO redo networking
 */
class ClientPlayer(input: InputDevice, name: String, color: Color, color2: Color, val localId: Int) : Player(input,
        name,
        color, color2) {
    var remoteId: Int? = null

    //  @Suppress("unused")
    //  constructor() : this(null, "", Color.WHITE, -1)
}