package uk.co.electronstudio.retrowar

import uk.co.electronstudio.sdl2gdx.RumbleController

interface NetworkController: RumbleController {
    abstract val guestName: String
    abstract var player: Player?

}
