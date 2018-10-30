package uk.co.electronstudio.retrowar.utils

import uk.co.electronstudio.retrowar.App
import uk.co.electronstudio.retrowar.Logger
import java.io.File
import java.io.PrintStream
import java.time.LocalDateTime

/** Logs to file and to standard out */
class SimpleLogger :
    Logger {
    override fun error(
        message: String
    ) {
        println(
            message
        )
    }

    init {
        val logFile =
            File(
                App.LOG_FILE_PATH
            )
        val logStream =
            logFile.outputStream()
                .buffered()
        val outStream =
            PrintStream(
                ComboOutputStream(
                    logStream,
                    System.out
                ),
                true
            )
        System.setErr(
            outStream
        )
        System.setOut(
            outStream
        )
    }

    override fun initialize() {
    }

    override fun log(
        message: String
    ) {
        val caller =
            org.slf4j.helpers.Util.getCallingClass()
                .simpleName
        log(
            caller,
            message
        )
    }

    override fun log(
        caller: String,
        message: String
    ) {
        println(
            "[${LocalDateTime.now()}] [$caller] $message"
        )
    }
}
