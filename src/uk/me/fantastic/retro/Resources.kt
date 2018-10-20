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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.I18NBundle

/**
* Some useful images and fonts you can use rather than loading your own
 */
class Resources {

    companion object {

        private fun load(classpath: String, internal: String): FileHandle {
            val cFile = Gdx.files.classpath(classpath)
            val iFile = Gdx.files.internal(internal)
            return if (iFile.exists()) iFile else cFile
        }

        // var defaultLocale = java.util.Locale.getDefault()
        //  var locale = Locale.Builder().setLocale(defaultLocale).setVariant("PROFANE").build()

        var TEXT = I18NBundle.createBundle(load("uk/me/fantastic/retro/i18n/RetroWar", "i18n/RetroWar"))
        @JvmStatic
        val MISSING_TEXTURE = Texture(Gdx.files.classpath("uk/me/fantastic/retro/badlogic.jpg"))
        @JvmStatic
        val MISSING_TEXTUREREGION = TextureRegion(MISSING_TEXTURE)

        private var fallbackFont = "uk/me/fantastic/retro/english.fnt"
        @JvmStatic
        val FONT = BitmapFont(load(fallbackFont,TEXT["fontBlack"]))
        @JvmStatic
        val FONT_CLEAR = BitmapFont(load(fallbackFont,TEXT["font"]))
        @JvmStatic
        val FONT_ENGLISH = BitmapFont(load(fallbackFont,"english.fnt"))
        @JvmStatic
        val BLING = Gdx.audio.newSound(load("uk/me/fantastic/retro/bling.wav", "bling.wav"))!!

        val palette = arrayOf(
                Color(0, 0, 0), // 0: black
                Color(157, 157, 157), // 1: grey
                Color(255, 255, 255), // 2: white
                Color(190, 38, 51), // 3: red
                Color(224, 111, 139), // 4: pink
                Color(73, 60, 43),   // 5: dbrown
                Color(164, 100, 34), // 6: lbrown
                Color(235, 137, 49), // 7: orange
                Color(247, 226, 107), // 8: yellow
                Color(47, 72, 78),   // 9: unknown
                Color(68, 137, 26),  // 10: dgreen
                Color(163, 206, 39), // 11: lgreen
                Color(27, 38, 50), // 12: ddblue
                Color(0, 87, 132), // 13: dblue
                Color(49, 162, 242), // 14: blue
                Color(178, 220, 239) // 15: lblue
        )
    }
}
