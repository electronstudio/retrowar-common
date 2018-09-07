package uk.me.fantastic.retro

import com.badlogic.gdx.Gdx
import uk.me.fantastic.retro.screens.SimpleTitleScreen

class SimpleApp(callback: Callback, val name: String, val factory: AbstractGameFactory, logger: Logger, manualGC:
ManualGC? = null, val advertise: Boolean = false) : App
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
        Prefs.BinPref.FULLSCREEN.enable()
        Prefs.BinPref.VSYNC.enable()
        Prefs.MultiChoicePref.LIMIT_FPS.set(0)
//        BinPrefMenuItem("motion ", BinPref.SMOOTH),
//        BinPrefMenuItem("pixels ", BinPref.BILINEAR),
//        BinPrefMenuItem("scaling ", BinPref.STRETCH),
//        BinPrefMenuItem("scanlines ", BinPref.SCANLINES),
        Prefs.BinPref.FPS.disable()
    }
}
