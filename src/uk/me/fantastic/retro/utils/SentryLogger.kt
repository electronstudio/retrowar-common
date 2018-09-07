package uk.me.fantastic.retro.utils

import com.badlogic.gdx.Gdx
import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder
import uk.me.fantastic.retro.App
import uk.me.fantastic.retro.Logger
import uk.me.fantastic.retro.Prefs
import java.io.File
import java.io.PrintStream
import java.time.LocalDateTime

class SentryLogger : Logger {
    override fun error(message: String) {
        println(message)
        Sentry.capture(message)
    }

    init {
        val logFile = File(App.LOG_FILE_PATH)
        val logStream = logFile.outputStream().buffered()
        val outStream = PrintStream(ComboOutputStream(logStream, System.out), true)
        System.setErr(outStream)
        System.setOut(outStream)
    }

    override fun initialize() {

        System.setProperty("sentry.dist", Gdx.app.type.name)
        // val keyGen1 = KeyPairGenerator.getInstance("DIFFIEHELLMAN")
        System.setProperty("sentry.release", App.app.versionString)
        println("sentry.release ${App.app.versionString} sentry.dist ${Gdx.app.type.name}")

        if (Prefs.BinPref.CRASH_REPORTS.isEnabled()) Sentry.init()

//        Sentry.getContext().recordBreadcrumb(
//                BreadcrumbBuilder().setMessage("User made an action").build()
//        )
//
//        Sentry.getContext().user = UserBuilder().setEmail("hello@sentry.io").build()
//        Sentry.getContext().addExtra("extra", "thing")
//        Sentry.getContext().addTag("tagName", "tagValue")

        // Sentry.capture("This is a test2")
    }

    override fun log(message: String) {
        val caller = org.slf4j.helpers.Util.getCallingClass().simpleName
        log(caller, message)
    }

    override fun log(caller: String, message: String) {
        println("[${LocalDateTime.now()}] [$caller] $message")
        Sentry.getContext().recordBreadcrumb(
                BreadcrumbBuilder().setMessage(message).setCategory(caller).build()
        )
    }
}
