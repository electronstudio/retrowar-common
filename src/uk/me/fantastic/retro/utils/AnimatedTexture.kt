package uk.me.fantastic.retro.utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

class AnimatedTexture(
    val delay: Float,
    vararg tex: TextureRegion,
    val mode: PlayMode = PlayMode.LOOP
) :
        Animation<TextureRegion>(delay, *tex) {
    constructor(delay: Float, t: Array<Array<TextureRegion>>, default: IntRange, mode: PlayMode = PlayMode.LOOP) :
            this(delay, *(default.map { t[0][it] }.toTypedArray()), mode = mode)

    constructor (delay: Float, file: String, width: Int, height: Int, default: IntRange, mode: PlayMode = PlayMode.LOOP) :
            this(delay, TextureRegion.split(Texture(file), width, height), default, mode)

    constructor(delay: Float, sheet: SpriteSheet, name: String, mode: PlayMode = PlayMode.LOOP) :
    //  sheet.getTextureRegion(name, frames)
            this(delay, *(sheet.getFrames(name).toTypedArray()), mode = mode)

    init {
        if (keyFrames.size < 1) throw ExceptionInInitializerError("cant have an Animation with no frames")
        tex.forEach { it.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest) }
        playMode = mode
    }

    companion object {
        // fixme i feel there should be more elegant solution
        fun AnimatedTextureOrNull(delay: Float, vararg tex: TextureRegion): AnimatedTexture? {
            var a: AnimatedTexture? = null
            try {
                a = AnimatedTexture(delay, *tex)
            } catch (e: ExceptionInInitializerError) {
            }
            return a
        }
    }
}