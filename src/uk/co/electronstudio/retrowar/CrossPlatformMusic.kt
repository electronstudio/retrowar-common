package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx

abstract class CrossPlatformMusic {
    companion object {
        fun create(desktopFile: String, androidFile: String, iOSFile: String): CrossPlatformMusic {
            if (Gdx.app.type == Application.ApplicationType.Android) {
                return LongSongPlayer(androidFile)
            } else if (Gdx.app.type == Application.ApplicationType.iOS) {
                return ShortSongPlayer(iOSFile)
            } else {
                return ShortSongPlayer(desktopFile)
            }
        }
    }

    open fun setPitch(pitch: Float) {} // do nothing if pitch change isnt supported
    abstract fun play()
    abstract fun stop()
    abstract fun dispose()

    class LongSongPlayer(val file: String) : CrossPlatformMusic() {
        val music = Gdx.audio.newMusic(Gdx.files.internal(file))

        init {
            music.isLooping = true
        }

        override fun play() {
            music.volume = Prefs.NumPref.MUSIC_VOLUME.asVolume()
            music.play()
        }

        override fun stop() {
            music.stop()
        }

        override fun dispose() {
            music.stop()
            music.dispose()
        }
    }

    class ShortSongPlayer(val file: String) : CrossPlatformMusic() {
        val music = Gdx.audio.newSound((Gdx.files.internal(file)))
        var musicId: Long? = null

        override fun setPitch(pitch: Float) {
            musicId?.let {
                music.setPitch(it, pitch)
            }
        }

        override fun play() {
            musicId = music.loop(Prefs.NumPref.MUSIC_VOLUME.asVolume(), 1.0f, 0.0f)
        }

        override fun stop() {
            music.stop()
        }

        override fun dispose() {
            music.stop()
            music.dispose()
        }
    }
}