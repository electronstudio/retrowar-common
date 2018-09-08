// <editor-fold desc="Copyright 2018 Richard Smith">
/*
    Copyright 2018 Richard Smith.

    RetroWar is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    RetroWar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with RetroWar.  If not, see <http://www.gnu.org/licenses/>.
*/
// </editor-fold>
package uk.me.fantastic.retro

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import uk.me.fantastic.retro.input.InputDevice
import uk.me.fantastic.retro.input.NetworkInput
import uk.me.fantastic.retro.network.ClientPlayer
import uk.me.fantastic.retro.utils.sqrt
import java.util.ArrayList
import kotlin.math.roundToInt

/*
 * Global scope stuff that IntelliJ doesn't like being in the actual files where it is used
 */

/** provides methods to output informative messages and errors
 * different implementations might show them or send them off
 * to a server */
interface Logger {
    fun log(message: String)
    fun log(caller: String, message: String)
    fun error(message: String)
    fun initialize()
}

val osName = System.getProperty("os.name")
val isOSX: Boolean = osName.contains("OS X")
val isMobile = Gdx.app.type == Application.ApplicationType.Android || Gdx.app.type == Application.ApplicationType.iOS
val isLinux: Boolean = osName.contains("Linux") && !isMobile
val isWindows: Boolean = !isLinux && !isOSX && !isMobile

/* GDX on iOS has very poor garbage collection.  Supply one of these to disable
* it and do your own GC at appropriate points when delays wont be noticed.
*/
interface ManualGC {
    fun enable()
    fun disable()
    fun doGC()
}


@Suppress("NOTHING_TO_INLINE")
inline fun log(log: String) {
    if (Prefs.BinPref.DEBUG.isEnabled()) {
        App.app.logger.log(log)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun log(c: String, log: String) {
    if (Prefs.BinPref.DEBUG.isEnabled()) {
        App.app.logger.log(c, log)
    }
}

fun error(message: String) {
    App.app.logger.error(message)
}

fun drawBox(
    MARGIN: Int,
    SHADOW_OFFSET: Int,
    shape: ShapeRenderer,
    width: Float,
    height: Float,
    y: Float,
    SCREEN_WIDTH: Float
) {
    val box = Rectangle(0f, 0f, width + MARGIN, height + MARGIN)
    shape.begin(ShapeRenderer.ShapeType.Filled)
    shape.color = Color.BLACK
    shape.rect(SCREEN_WIDTH / 2 - box.width / 2 + SHADOW_OFFSET, y - SHADOW_OFFSET - box.height + MARGIN / 2, box.width, box
            .height)
    shape.color = Color(0, 87, 132)
    shape.rect(SCREEN_WIDTH / 2 - box.width / 2, y - box.height + MARGIN / 2, box.width, box.height)
    shape.end()
    shape.begin(ShapeRenderer.ShapeType.Line)
    shape.color = Color.WHITE
    shape.rect(SCREEN_WIDTH / 2 - box.width / 2, y - box.height + MARGIN / 2, box.width, box.height)
    shape.end()
}

fun listAllLevels() = Gdx.files.internal("levels").list().filter { it.extension() == "tmx" }.map { it.name().dropLast(4) }

fun Float.roundDown(): Float {
    return MathUtils.floor(this).toFloat()
}

/** @suppress */
enum class Size {
    SMALL, MEDIUM, LARGE
}

/** @suppress */
class JoinRequest

/** @suppress */
class WorldUpdate(val buffer: ByteArray?, val id: Int) {
    constructor() : this(null, 0)
}

/** @suppress */
class PlayersUpdate(val players: ArrayList<Player>?) {
    constructor() : this(null)
}

/** @suppress */
class CreatePlayerRequest(val player: ClientPlayer?) {
    constructor() : this(null)
}

/** @suppress */
class InputUpdate(input: InputDevice?, val playerId: Int) {

    constructor() : this(null, -1)

    var networkInput: NetworkInput? = null

    init {
        if (input != null) {
            networkInput = NetworkInput(input.leftStick, input.rightStick, input.leftTrigger, input.rightTrigger,
                    input.A)
        }
    }
}

/** @suppress */
class CreatePlayerResponse(val serverPlayerId: Int, val clientPlayerId: Int) {
    constructor() : this(-1, -1)
}

fun Pair<Float, Float>.normVector(): Pair<Float, Float> {

    val vMagnitude = (first * first + second * second).sqrt()
    return Pair(first / vMagnitude, second / vMagnitude)
}

interface Callback {
    fun setForegroundFPS(foregroundFPS: Int)
    fun setBackgroundFPS(backgroundFPS: Int)
}

class EmptyCallback : Callback {

    override fun setForegroundFPS(foregroundFPS: Int) {
    }

    override fun setBackgroundFPS(backgroundFPS: Int) {
    }
}

typealias Square = ArrayList<Int>

fun Color(r: Int, g: Int, b: Int): Color {
    return Color(r.toFloat() / 255f, g.toFloat() / 255f, b.toFloat() / 255f, 1.0f)
}

/*
FIXME in Java this would be a performance improvement over ArrayList; in Kotlin I don't know how to avoid
autoboxing when I pull the ints out of the array so this might be what causes lots of Integers to be allocated!
It might be better just to use Array<Integer>. There's also IntArray but that lacks a clear()
*/
// class MyIntArray : com.badlogic.gdx.utils.IntArray(false, 256), Iterable<Int> {
//    override fun iterator(): Iterator<Int> {
//        return object : Iterator<Int> {
//            var i = 0
//            override fun next(): Int {
//                return items[i++]
//            }
//
//            override fun hasNext(): Boolean {
//                return i < size
//            }
//
//        }
//    }
//
// }

inline fun <reified T> matrix2d(height: Int, width: Int, init: (Int, Int) -> Array<T>) = Array(height, { row -> init(row, width) })

fun Float.round(): Float = roundToInt().toFloat()

fun createDefaultShader(): ShaderProgram {
    val vertexShader = ("in vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" + //
            "in vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" + //
            "in vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" + //
            "uniform mat4 u_projTrans;\n" + //
            "out vec4 v_color;\n" + //
            "out vec2 v_texCoords;\n" + //
            "\n" + //
            "void main()\n" + //
            "{\n" + //
            "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" + //
            "   v_color.a = v_color.a * (255.0/254.0);\n" + //
            "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" + //
            "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" + //
            "}\n")
    val fragmentShader = ("#ifdef GL_ES\n" + //
            "#define LOWP lowp\n" + //
            "precision mediump float;\n" + //
            "#else\n" + //
            "#define LOWP \n" + //
            "#endif\n" + //
            "in LOWP vec4 v_color;\n" + //
            "in vec2 v_texCoords;\n" + //
            "out vec4 fragColor;\n" + //
            "uniform sampler2D u_texture;\n" + //
            "void main()\n" + //
            "{\n" + //
            "  fragColor = v_color * texture(u_texture, v_texCoords);\n" + //
            "}")

    ShaderProgram.prependFragmentCode = "#version 330\n"
    ShaderProgram.prependVertexCode = "#version 330\n"
    val shader = ShaderProgram(vertexShader, fragmentShader)
    if (shader.isCompiled == false) throw IllegalArgumentException("Error compiling shader: " + shader.log)
    return shader
}

private fun createVertexShader(hasNormals: Boolean, hasColors: Boolean, numTexCoords: Int): String {
    var shader = ("in vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
            (if (hasNormals) "in vec3 " + ShaderProgram.NORMAL_ATTRIBUTE + ";\n" else "") +
            if (hasColors) "in vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" else "")

    for (i in 0 until numTexCoords) {
        shader += "in vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + i + ";\n"
    }

    shader += "uniform mat4 u_projModelView;\n"
    shader += if (hasColors) "out vec4 v_col;\n" else ""

    for (i in 0 until numTexCoords) {
        shader += "out vec2 v_tex$i;\n"
    }

    shader += ("void main() {\n" + "   gl_Position = u_projModelView * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
            if (hasColors) "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" else "")

    for (i in 0 until numTexCoords) {
        shader += "   v_tex" + i + " = " + ShaderProgram.TEXCOORD_ATTRIBUTE + i + ";\n"
    }
    shader += "   gl_PointSize = 1.0;\n"
    shader += "}\n"
    return shader
}

private fun createFragmentShader(hasColors: Boolean, numTexCoords: Int): String {
    var shader = "#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n"

    if (hasColors) shader += "in vec4 v_col;\n"
    for (i in 0 until numTexCoords) {
        shader += "in vec2 v_tex$i;\n"
        shader += "uniform sampler2D u_sampler$i;\n"
    }
    shader += "out vec4 fragColor;\n"

    shader += "void main() {\n" + "   fragColor = " + if (hasColors) "v_col" else "vec4(1, 1, 1, 1)"

    if (numTexCoords > 0) shader += " * "

    for (i in 0 until numTexCoords) {
        if (i == numTexCoords - 1) {
            shader += " texture(u_sampler$i,  v_tex$i)"
        } else {
            shader += " texture(u_sampler$i,  v_tex$i) *"
        }
    }

    shader += ";\n}"
    return shader
}

/** Returns a new instance of the default shader used by SpriteBatch for GL2 when no shader is specified.  */
fun createDefaultShapeShader(hasNormals: Boolean = false, hasColors: Boolean = true, numTexCoords: Int = 0):
        ShaderProgram {
    val vertexShader = createVertexShader(hasNormals, hasColors, numTexCoords)
    val fragmentShader = createFragmentShader(hasColors, numTexCoords)
    ShaderProgram.prependFragmentCode = "#version 330\n"
    ShaderProgram.prependVertexCode = "#version 330\n"
    val shader = ShaderProgram(vertexShader, fragmentShader)
    if (shader.isCompiled == false) throw IllegalArgumentException("Error compiling shader: " + shader.log)
    return shader
}

fun renderTileMapToTexture(map: TiledMap): TextureRegion {
    val tiles = map.layers[0] as TiledMapTileLayer
    val width = tiles.width * tiles.tileWidth
    val height = tiles.height * tiles.tileHeight
    val batch = SpriteBatch(1000, createDefaultShader())
    val mapRenderer = OrthogonalTiledMapRenderer(map, 1f, SpriteBatch(1000, createDefaultShader()))

    val fbo = FrameBuffer(Pixmap.Format.RGB888, width.toInt(), height.toInt(), false)

    val fboCam = OrthographicCamera(width, height)
    val fboTexture = TextureRegion(fbo.colorBufferTexture)

    fboTexture.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)

    fboTexture.flip(false, true) // for some reason y-axis is inverted in framebuffer

    fboCam.position.set(width / 2f, height / 2f, 0f)
    fboCam.update()

    fbo.begin()

    Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    batch.projectionMatrix = fboCam.combined

    batch.begin()

    mapRenderer.setView(fboCam)
    mapRenderer.render()

    batch.end()

    fbo.end()

    return fboTexture
}