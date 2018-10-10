package uk.me.fantastic.retro.utils

import com.badlogic.gdx.Gdx
import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder
import uk.me.fantastic.retro.Logger

/** Logging for Android build.  Attempts to use Sentry too if its configured
 *
 * @param version Version string of app
 * @param dsn The private(ish) code that identifies your app to Sentry
 * */
class AndroidLogger(val version: String, val dsn: String) : Logger {
    override fun error(message: String) {
        println(message)
        Sentry.capture(message)
    }

    override fun log(message: String) {
        println("$message")
        Sentry.getContext().recordBreadcrumb(
                BreadcrumbBuilder().setMessage(message).build()
        )
    }

    override fun log(caller: String, message: String) {
        log(message)
    }

    override fun initialize() {
        System.setProperty("sentry.dist", Gdx.app.type.name)
        System.setProperty("sentry.release", version)
        println("sentry.release $version sentry.dist ${Gdx.app.type.name}")
        Sentry.init(dsn)
    }
}
