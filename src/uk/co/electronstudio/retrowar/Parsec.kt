package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GLTexture
import com.parsecgaming.parsec.*
import com.sun.jna.Memory

import kong.unirest.Unirest
import uk.co.electronstudio.parsec.InputEvent
import uk.co.electronstudio.parsec.ParsecHostListener
import uk.co.electronstudio.parsec.ParsecLogListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue


class Parsec : ParsecHostListener, ParsecLogListener {


    val nameString = "RetroWar: 8-Bit Party Battle"



    enum class State(val msg: String) {
        STOPPED("STOPPED"), HOSTING_DESKTOP("[GREEN]HOSTING (D)[]"), HOSTING_GAME("[GREEN]HOSTING (G)[]"), INVALID_SESSION_ID(
            "[RED]INVALID SESSION ID\nPLEASE LOGIN AGAIN[]")  //fixme sealed classes would be nicer than enum
    }

    @Volatile
    var state: State = State.STOPPED

    val messages = ConcurrentLinkedQueue<String>()

    private val parsec = uk.co.electronstudio.parsec.Parsec(this, true, 9000, 8000)




    val guests = ConcurrentHashMap<Int, String>()




    fun getPointer() = parsec.parsecPointer


    private val parsecHostConfig = ParsecLibrary.ParsecHostDefaults()


    init {

        parsecHostConfig.encoderFPS = 60
        parsecHostConfig.maxGuests = 15



//        Gdx.app.postRunnable(object : Runnable {
//            override fun run() {
//                try {
//                    if (state == State.HOSTING_GAME) pollInput()
//                } catch (e: Throwable) {
//                    e.printStackTrace()
//                }
//
//                Gdx.app.postRunnable(this)
//            }
//        })

    }

    fun pollInput() {
        if(state != State.HOSTING_GAME) return
        for (event in parsec.hostPollInput()) {
            val controller = App.app.parsecControllers[event.guestId]
            when (event) {
                is InputEvent.GamepadButtonEvent -> {
                    controller?.buttonState
                }
            }
            controller?.processMessage(event)
        }


    }





    fun hostDesktopStart(id: String) {
        if (state == State.STOPPED || state == State.INVALID_SESSION_ID) {
            val statusCode = parsec.hostStartDesktop(parsecHostConfig, this, nameString, id, Prefs.NumPref.PARSEC_LAST_SERVER_ID.getNum())
            log("parsec", "ParsecHostStar result $statusCode")

            if (statusCode >= 0) {
                state = State.HOSTING_DESKTOP
            }
        }

    }

    fun hostGameStart(id: String) {
        if (state == State.STOPPED || state == State.INVALID_SESSION_ID) {
            val statusCode = parsec.hostStartGame(parsecHostConfig, this, nameString, id, Prefs.NumPref.PARSEC_LAST_SERVER_ID.getNum())
            log("parsec", "ParsecHostStar result $statusCode")

            if (statusCode >= 0) {
                state = State.HOSTING_GAME
            }

        }
    }


    //    fun login(email: String, password: String, tfa: String) {
    //        val text = "{\"email\": \"$email\", \"password\": \"$password\", \"tfa\": \"$tfa\"}"
    //        Unirest.post("https://parsecgaming.com/v1/auth/").header("Content-Type", "application/json")
    //            .body(text).asJsonAsync({ response ->
    //                val code = response.getStatus()
    //                val body: JsonNode = response.getBody()
    //                loginResult=body.toString()
    //                    log("Parec", "login result $code $body")
    //            })
    //    }

    fun loginSync(email: String, password: String, tfa: String): Pair<Int, String> {
        val text = "{\"email\": \"$email\", \"password\": \"$password\", \"tfa\": \"$tfa\"}"
        val request =
            Unirest.post("https://parsecgaming.com/v1/auth/").header("Content-Type", "application/json").body(text)
                .asJson()
        val loginResult = request.body.toString()
        return Pair(request.status, loginResult)
    }


    fun stop() {
        state = State.STOPPED
        parsec.hostStop()
    }

    fun status(): String = if (parsec.statusCode < 0) "[RED]ERROR ${parsec.statusCode}[]" else "${state.msg}"

    fun submitFrame(texture: GLTexture) {
        if (state == State.HOSTING_GAME) {
            parsec.submitFrame(texture.textureObjectHandle)
        }
    }

    fun submitAudio(rate: Int, pcm: ByteArray, samples: Int) {
        if (state == State.HOSTING_GAME) {
            val buffer = Memory(pcm.size.toLong())
            buffer.write(0L, pcm, 0, pcm.size)
            ParsecLibrary.ParsecHostSubmitAudio(parsec.parsecPointer, ParsecLibrary.ParsecPCMFormat.PCM_FORMAT_INT16, rate, buffer, samples)
        }
    }

    override fun guestConnected(id: Int, name: String, attemptID: ByteArray) {
        guests.put(id, name)
        val controller = ParsecController(id, name)
        App.app.parsecControllers.put(id, controller)
        //ParsecController()
        messages.add("$name connected")
    }

    override fun guestDisconnected(id: Int, name: String, attemptID: ByteArray) {
        guests.remove(id)
        App.app.parsecControllers.remove(id)
        messages.add("$name disconnected")
    }

    override fun guestConnecting(id: Int, name: String, attemptID: ByteArray) {

    }

    override fun guestFailed(id: Int, name: String, attemptID: ByteArray) {
        messages.add("$name failed to connect")
    }

    override fun guestWaiting(id: Int, name: String, attemptID: ByteArray) {
        parsec.hostAllowGuest(attemptID, true)
    }

    override fun invalidSessionId() {
        log("Parsec", "invalidSessionID")
        state = Parsec.State.INVALID_SESSION_ID
    }

    override fun serverId(hostID: Int, serverID: Int) {
        log("Parsec", "serverID $hostID $serverID")
        Gdx.app.postRunnable {
            Prefs.NumPref.PARSEC_LAST_SERVER_ID.setNum(serverID)
        }
    }

    override fun userData(guest: ParsecGuest, id: Int, text: String) {
        val name = String(guest.name)
        messages.add("$name: ${text}")
        log("Parsec", "userdata $id ${text} ${guest.id} ${guest.attemptID} ${name} ${guest.state}")

    }



    override fun log(level: Int, msg: String) {
        log("Parsec", "log $msg")
    }



}
