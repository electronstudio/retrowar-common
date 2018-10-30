package uk.co.electronstudio.retrowar.utils

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import uk.co.electronstudio.retrowar.Callback

/**
 * used to change FPS while app is running without restarting
 */
class DesktopCallback :
    Callback {

    val config =
        LwjglApplicationConfiguration()

    init {
        config.vSyncEnabled =
                true
        config.audioDeviceBufferSize =
                1024
        config.audioDeviceBufferCount =
                32
        config.audioDeviceSimultaneousSources =
                32
        config.useGL30 =
                true
        config.gles30ContextMajorVersion =
                3
        config.gles30ContextMinorVersion =
                3

        // //   config.useVsync(true)
        //     config.setIdleFPS(30)
        //     config.
        // config.foregroundFPS = 30
        //  config.backgroundFPS = 30
        //       config.width = 1920
//        config.height = 1200
//        config.fullscreen = true

        //       config.fullscreen= true
//      config.width = 1800
//        config.height = 600
    }

    override fun setForegroundFPS(
        foregroundFPS: Int
    ) {
        config.foregroundFPS =
                foregroundFPS
    }

    override fun setBackgroundFPS(
        backgroundFPS: Int
    ) {
        config.backgroundFPS =
                backgroundFPS
    }
}