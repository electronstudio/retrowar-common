package uk.me.fantastic.retro.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.obj

class SpriteSheet(val file: String) {

    private val frames: JsonObject
    private val sheet: Texture

    init {
        val json = Parser().parse(Gdx.files.internal(file).reader()) as JsonObject
        val meta = json["meta"] as JsonObject
        val fn = meta["image"] as String
        sheet = Texture(fn)
        frames = json["frames"] as JsonObject
    }

    fun getTextureRegion(name: String, num: Int): TextureRegion {
        return getTextureRegion("$name$num")
    }

    fun getFrames(tag: String): List<TextureRegion> {
        return frames.filter { it.key.startsWith(tag + "-") }.map { getTextureRegion(it.key) }
    }

    fun getFrameDelays(tag: String): List<Float> {
        return frames.filter { it.key.startsWith(tag + "-") }.map { getFrameDelay(it.key) }
    }

    private fun getFrameDelay(r: String): Float {
        val json = frames.obj(r) as JsonObject
        val ms = json["duration"] as Int

        return ms.toFloat() / 1000f
    }

    private fun getTextureRegion(r: String): TextureRegion {
        val frame = frames.obj(r)?.obj("frame") as JsonObject
        val x = frame["x"] as Int
        val y = frame["y"] as Int
        val w = frame["w"] as Int
        val h = frame["h"] as Int
        return TextureRegion(sheet, x, y, w, h)
    }

    fun getAnim(name: String): AnimatedTexture {
        val d = getFrameDelays(name).first()
        return AnimatedTexture(d, this, name)
    }
}