package uk.co.electronstudio.sdl2gdx

import com.badlogic.gdx.controllers.Controller

interface RumbleController: Controller{
     fun rumble(leftMagnitude: Float, rightMagnitude: Float, duration_ms: Int): Boolean
}
