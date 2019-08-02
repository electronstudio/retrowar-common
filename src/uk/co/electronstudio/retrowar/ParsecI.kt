package uk.co.electronstudio.retrowar

import com.badlogic.gdx.graphics.GLTexture
import com.badlogic.gdx.graphics.Texture

interface ParsecI {
    abstract fun submitFrame(colorBufferTexture: GLTexture)

    abstract fun pollMessages(): String
    abstract fun dispose()


}
