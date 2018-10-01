package uk.me.fantastic.retro

import com.badlogic.gdx.Gdx
import uk.me.fantastic.retro.screens.SimpleTitleScreen

/**
 * Most standalone games will use this as their main GDX or Android Application class
 * It sets up a simple title screen and menus
 */
class SimpleApp(callback: Callback, val name: String, val factory: AbstractGameFactory, logger: Logger, manualGC:
ManualGC? = null, val advertise: Boolean = false, val fullscreen: Boolean = true) : App
(callback, logger, manualGC) {

    override fun quit() {
        log("App", "Quit")
        Gdx.app.exit()
    }

    override fun create() {

        log("SimpleApp from $factory create")
        setScreenMode()

        initialiseAndroid()
        initialiseDesktop()
        setPrefsToDefaultsForSingleGames()
        initialisePrefs()
        initializeInput()
        initialiseControllers()
        initialiseShader()

        if(advertise){
            title = SimpleTitleScreen(title = name, factory = factory, quitText = "More RetroWar", quitURL =
            "https://store.steampowered.com/app/664240/)")
        }else {
            title = SimpleTitleScreen(title = name, factory = factory)
        }
        setScreen(title)
    }

    fun setPrefsToDefaultsForSingleGames() {
        if(fullscreen) {
            Prefs.BinPref.FULLSCREEN.enable()
        }else{
            Prefs.BinPref.FULLSCREEN.disable()
        }
        Prefs.BinPref.VSYNC.enable()
        Prefs.MultiChoicePref.LIMIT_FPS.set(0)
//        BinPrefMenuItem("motion ", BinPref.SMOOTH),
//        BinPrefMenuItem("pixels ", BinPref.BILINEAR),
//        BinPrefMenuItem("scaling ", BinPref.STRETCH),
//        BinPrefMenuItem("scanlines ", BinPref.SCANLINES),
        Prefs.BinPref.FPS.disable()
    }
}
