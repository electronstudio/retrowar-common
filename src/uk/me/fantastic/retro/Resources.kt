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
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.I18NBundle

class Resources {

    companion object {

        var baseFileHandle = Gdx.files.internal("i18n/RetroWar")
        var defaultLocale = java.util.Locale.getDefault()

        //  var locale = Locale.Builder().setLocale(defaultLocale).setVariant("PROFANE").build()

        var TEXT = I18NBundle.createBundle(baseFileHandle)

        val MISSING_TEXTURE = Texture("badlogic.jpg")
        val MISSING_TEXTUREREGION = TextureRegion(MISSING_TEXTURE)

        private fun TextureRegion(s: String): TextureRegion = TextureRegion(Texture(s))

//        val FONT = BitmapFont(Gdx.files.internal("c64_low3_black.fnt"))
//        val FONT_CLEAR = BitmapFont(Gdx.files.internal("c64_low3.fnt"))

        val FONT = BitmapFont(Gdx.files.internal(TEXT["fontBlack"]))
        val FONT_CLEAR = BitmapFont(Gdx.files.internal(TEXT["font"]))

        val FONT_ENGLISH = BitmapFont(Gdx.files.internal("english.fnt"))

        val BLING = Gdx.audio.newSound(Gdx.files.internal("powerup.wav"))!!
    }
}
