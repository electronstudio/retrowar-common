package uk.co.electronstudio.retrowar

import com.badlogic.gdx.graphics.Color

data class PlayerData(var name: String = "", var color: Color = Color(0, 0, 0), var color2: Color = Color(0, 0, 0), var controllerString: String = "", var controllerId: Int = -1)
