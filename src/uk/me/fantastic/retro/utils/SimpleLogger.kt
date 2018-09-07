package uk.me.fantastic.retro.utils

import uk.me.fantastic.retro.App
import uk.me.fantastic.retro.Logger
import java.io.File
import java.io.PrintStream
import java.time.LocalDateTime

class SimpleLogger : Logger {
    override fun error(message: String) {
        println(message)
    }

    init {
        val logFile = File(App.LOG_FILE_PATH)
        val logStream = logFile.outputStream().buffered()
        val outStream = PrintStream(ComboOutputStream(logStream, System.out), true)
        System.setErr(outStream)
        System.setOut(outStream)
    }

    override fun initialize() {
    }

    override fun log(message: String) {
        val caller = org.slf4j.helpers.Util.getCallingClass().simpleName
        log(caller, message)
    }

    override fun log(caller: String, message: String) {
        println("[${LocalDateTime.now()}] [$caller] $message")
    }
}
