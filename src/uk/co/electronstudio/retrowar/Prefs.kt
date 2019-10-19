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
package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import uk.co.electronstudio.retrowar.App.Companion.app

import uk.co.electronstudio.retrowar.Resources.Companion.TEXT
import uk.co.electronstudio.retrowar.Resources.Companion.palette

/**
 *  Stores stuff in GDX preference files, but also provides singleton/enums for use in creating option Menus
 */
object Prefs {

    var prefs: Preferences = Gdx.app.getPreferences(System.getProperty("retrowar.name", "retrowar-unknown-game"))

    val shaders = makeShaderList()

    val colors = palette.map { it.toString() }

    private fun makeShaderList(): List<String> {
        val shaderFiles = Gdx.files.internal("shaders").list("glsl").map { it.nameWithoutExtension() }
        return listOf("NONE") + shaderFiles
    }

    /** @suppress */
    enum class MultiChoicePref(val pref: String, vararg val choices: String, val default: Int = 0) {
        SERVER_MODE("serverMode","COMPATIBLE (DESKTOP CAPTURE)", "FULL INTEGRATION", default = 1),
        SERVER_PUBLIC("serverPublic","PRIVATE", "PUBLIC", default = 0),
        PARTICLES("particles", "ON", "OFF", "EXCESSIVE"),
        RUMBLE("rumble", "HIGH", "LOW", "OFF"),
        INPUT("input", "RAW INPUT", "XINPUT", "DIRECT INPUT"),
        GRAPHICS("graphics", "RETRO", "MODERN", "CRT") {
            override fun apply() {
                when (getNum()) {
                    0 -> {
                        SHADER.set(0)
                        app.initialiseShader()
                        BinPref.SMOOTH.disable()
                        BinPref.BILINEAR.enable()
                        BinPref.SCANLINES.enable()
                    }
                    1 -> {
                        SHADER.set(0)
                        app.initialiseShader()
                        BinPref.SMOOTH.enable()
                        BinPref.BILINEAR.disable()
                        BinPref.SCANLINES.disable()
                    }
                    else -> {
                        SHADER.set(1)
                        app.initialiseShader()
                        BinPref.SMOOTH.disable()
                        BinPref.BILINEAR.disable()
                        BinPref.SCANLINES.disable()
                    }
                }
            }
        },

        SHADER("shader", *shaders.toTypedArray()) {
            override fun apply() {
                app.initialiseShader()
                BinPref.BILINEAR.disable()
                BinPref.SMOOTH.disable()
                BinPref.SCANLINES.disable()
                BinPref.STRETCH.enable()
            }
        },
        PLAYER1_COLOR("player1_color",
            *(colors.map { it.toString() }.toTypedArray()),
            default = 9),
        PLAYER1_COLOR2("player1_color2",
            *(colors.map { it.toString() }.toTypedArray()),
            default = 15),
        PLAYER2_COLOR("player2_color",
            *(colors.map { it.toString() }.toTypedArray()),
            default = 3),
        PLAYER2_COLOR2("player2_color2",
            *(colors.map { it.toString() }.toTypedArray()),
            default = 4),
        PLAYER3_COLOR("player3_color",
            *(colors.map { it.toString() }.toTypedArray()),
            default = 13),
        PLAYER3_COLOR2("player3_color2",
            *(colors.map { it.toString() }.toTypedArray()),
            default = 14),
        PLAYER4_COLOR("player4_color",
            *(colors.map { it.toString() }.toTypedArray()),
            default = 10),
        PLAYER4_COLOR2("player4_color2",
            *(colors.map { it.toString() }.toTypedArray()),
            default = 11),
        PLAYERGUEST_COLOR("playerguest_color",
            *(colors.map { it.toString() }.toTypedArray()),
            default = 5),
        PLAYERGUEST_COLOR2("playerguest_color2",
            *(colors.map { it.toString() }.toTypedArray()),
            default = 7),
        LIMIT_FPS("limitfps", "0", "30", "60") {
            override fun apply() {
                App.app.setFPS(getString().toInt())
            }
        };

        fun next() {
            val n = getNum() + 1

            if (n > choices.lastIndex) {
                set(0)
            } else {
                set(n)
            }
        }

        fun prev() {
            val n = getNum() - 1
            if (n < 0) {
                set(choices.lastIndex)
            } else {
                set(n)
            }
        }

        fun set(n: Int) {
            prefs.putInteger(pref, MathUtils.clamp(n, 0, choices.lastIndex))
            prefs.flush()
            apply()
        }

        fun displayText(): String {
            return choices[prefs.getInteger(pref, default)]
        }

        fun getString(): String {
            return choices[prefs.getInteger(pref, default)]
        }

        fun getNum(): Int {
            return prefs.getInteger(pref, default)
        }

        fun reset() {
            set(default)
        }

        open fun apply() {}
    }

    /** @suppress */
    enum class BinPref(
        val pref: String,
        val text: String = pref,
        val tText: String = TEXT["on"],
        val fText: String = TEXT["off"],
        val default: Boolean = true
    ) {
        VSYNC("vsync") {
            override fun apply() {
                Gdx.graphics.setVSync(VSYNC.isEnabled())
            }
        },
        STRETCH("stretch", tText = TEXT["stretched"], fText = TEXT["pixelPerfect"], default = true) {
            override fun apply() {
                App.app.resize(Gdx.graphics.width, Gdx.graphics.height)
            }
        },
        SCANLINES("scanlines", default = true),
        SMOOTH("smooth",
            tText = "FAKE but SMOOTH",
            fText = "GENUINE",
            default = false),
        ANALOG_CONTOLRS("analog",
            tText = "MODERN (ANALOGUE STICK)",
            fText = "RETRO (8-WAY STICK)",
            default = true),
        SPLASH("splash", default = true), //        PROFANITY("profanity", default = false) {
        //            override fun apply() {
        //                var locale = Locale(Resources.defaultLocale.language, "", if (PROFANITY.isEnabled()) "profane" else "")
        //                Resources.TEXT = I18NBundle.createBundle(Resources.baseFileHandle, locale)
        //            }
        //        },
        BILINEAR("bilinear", tText = TEXT["on"], fText = TEXT["off"], default = true) {
            override fun apply() {}
        },
        DEBUG("debug", tText = TEXT["on"], fText = TEXT["off"], default = false) {
            override fun apply() {}
        },
        CRASH_REPORTS("crashreports", tText = TEXT["on"], fText = TEXT["off"], default = true) {
            override fun apply() {}
        },
        AUTOFIRE("autofire", tText = TEXT["on"], fText = TEXT["off"], default = false) {
            override fun apply() {}
        },
        MUSIC("music", tText = TEXT["on"], fText = TEXT["off"]) {
            override fun apply() {
            }
        },
        FPS("fps", default = false) {
            override fun apply() {}
        },
        FULLSCREEN("fullscreen", tText = TEXT["fullscreen"], fText = TEXT["windowed"]) {
            override fun apply() {
                app.setScreenMode()
            }
        };

        fun displayText(): String {
            if (isEnabled()) return tText
            else return fText
        }

        fun enable() {
            log("enabled " + this)
            prefs.putBoolean(pref, true)
            prefs.flush()
            apply()
        }

        fun disable() {
            log("disabled " + this)
            prefs.putBoolean(pref, false)
            prefs.flush()
            apply()
        }

        fun toggle() {
            log("toggled " + this)
            prefs.putBoolean(pref, !isEnabled())
            prefs.flush()
            apply()
        }

        fun isEnabled(): Boolean {
            return prefs.getBoolean(pref, default)
        }

        open fun apply() {}
        fun filter(img: TextureRegion) {
            filter(img.texture)
        }

        fun filter(tex: Texture) {
            if (BILINEAR.isEnabled()) tex.setFilter(Linear, Linear)
            else tex.setFilter(Nearest, Nearest)
        }
    }

    /** @suppress */
    enum class NumPref(
        val pref: String,
        val text: String = pref,
        val min: Int = 0,
        val max: Int = 0,
        val default: Int = 50,
        val step: Int = 1
    ) {
        PARSEC_LAST_SERVER_ID("parsecserverid", default = 0),
        SCREEN_SHAKE("screenshake", min = 0, max = 100, default = 30, step = 10),
        SHIP_SPEED("shipspeed",
            min = 100,
            max = 500,
            default = 180,
            step = 10),
        SHIP_ACC("shipacc", min = 100, max = 1000, default = 300, step = 10),
        BULLET_SPEED("bulletspeed",
            min = 100,
            max = 1000,
            default = 300,
            step = 10),
        BULLET_RATE("bulletrate", min = 1, max = 100, default = 20),
        SHIP_HEALTH("shiphealth",
            min = 1,
            max = 20,
            default = 10,
            step = 1),
        SHIP_KNOCKBACK("shipgnockback", min = 0, max = 40, default = 10, step = 1),
        FX_VOLUME("fxvolume",
            min = 0,
            max = 10,
            default = 10,
            step = 1),
        MUSIC_VOLUME("musicvolume", min = 0, max = 10, default = 10, step = 1) {
            override fun apply() {
                app.applyMusicVolume(asVolume())
            }
        },
        DEADZONE("deadzone", min = 0, max = 25, default = 15, step = 1),
        BUFFER("buffer", min = 0, max = 20, default = 6, step = 1);

        fun displayText(): String {
            return "${prefs.getInteger(pref, default)}"
        }

        fun asVolume(): Float {
            val a = getNum().toFloat() / 10f
            return a * a * a
        }

        fun asPercent(): Float {
            return getNum().toFloat() / 100f
        }

        fun getNum(): Int {
            return prefs.getInteger(pref, default)
        }

        fun setNum(num : Int){
            prefs.putInteger(pref, num)
            prefs.flush()
            log("pref $name set to $num")
        }

        fun increase() {
            var i = prefs.getInteger(pref, default)
            if (i < max) {
                i += step

                prefs.putInteger(pref, i)
                prefs.flush()
                log("pref $name set to $i")
            }
            apply()
        }

        fun decreass() {
            var i = prefs.getInteger(pref, default)
            if (i > min) {
                i -= step
                prefs.putInteger(pref, i)
                prefs.flush()
                log("pref $name set to $i")
            }
            apply()
        }

        open fun apply() {}
    }

    /** @suppress */
    enum class StringPref(val pref: String, val text: String = pref, val default: String = "") {
        SERVER("server", default = "1.1.1.1"),
        PLAYER_MORE("playermore", default = "PLAYER"),
        PARSEC_SESSIONID("parsecsessionid", default="");

        fun displayText(): String {
            return prefs.getString(pref, default)
        }

        fun getString(): String {
            return prefs.getString(pref, default)
        }

        fun setString(s: String) {
            prefs.putString(pref, s)
            prefs.flush()
        }

        fun appendChar(c: Char) {
            setString(getString() + c)
        }

        fun deleteChar() {
            setString(getString().dropLast(1))
        }

        fun reset() {
            setString(default)
        }
    }
}
