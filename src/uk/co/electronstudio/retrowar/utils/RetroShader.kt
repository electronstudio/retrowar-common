package uk.co.electronstudio.retrowar.utils

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import uk.co.electronstudio.retrowar.createDefaultShaderGL2
import uk.co.electronstudio.retrowar.log

/** Loads shaders and sets up some vertex attributes format of RetroArch shaders */
class RetroShader(
    filename: String
) {

    var shader: ShaderProgram? =
        null

    init {
        // important since we aren't using some uniforms and attributes that SpriteBatch expects
        val version =
            if (Gdx.app.type == Application.ApplicationType.Android) "" else "#version 330\n"

        ShaderProgram.pedantic =
                false
        ShaderProgram.prependVertexCode = version +
                "#define VERTEX\n#define MVPMatrix u_projTrans\n" +
                "#define VertexCoord a_position\n" +
                "#define TexCoord a_texCoord0\n" +
                "#define COLOR a_color\n" +
                "#define Texture u_texture\n"
        ShaderProgram.prependFragmentCode = version +
                "#define FRAGMENT\n#define MVPMatrix u_projTrans\n" +
                "#define VertexCoord a_position\n" +
                "#define TexCoord a_texCoord0\n" +
                "#define COLOR a_color\n" +
                "#define Texture u_texture\n"

        val file =
            Gdx.files.internal(
                filename
            )
        if (file.exists()) {
            attemptToLoadShader(
                file
            )
        }
    }

    private fun attemptToLoadShader(
        file: FileHandle?
    ) {
        val potentialShader =
            ShaderProgram(
                file,
                file
            )
        if (potentialShader.log.isNotEmpty()) {
            log(potentialShader.log)
        }
        if (potentialShader.isCompiled) {
            this.shader =
                    potentialShader
        } else {
            this.shader =
                    null
            uk.co.electronstudio.retrowar.error(
                "failed to compile shader"
            )
        }
    }

    fun process(
        fboBatch: SpriteBatch,
        textureSize: Vector2,
        inputSize: Vector2,
        outputSize: Vector2
    ) {
        fboBatch.shader =
                shader
        shader?.let {
            it.setUniformf(
                "TextureSize",
                textureSize
            )
            it.setUniformf(
                "InputSize",
                inputSize
            )
            it.setUniformf(
                "OutputSize",
                outputSize
            )
        }
    }
}