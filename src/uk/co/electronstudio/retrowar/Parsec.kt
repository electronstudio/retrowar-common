package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GLTexture
import com.parsecgaming.parsec.*
import com.sun.jna.Memory
import com.sun.jna.Pointer
import kong.unirest.Unirest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue


class Parsec {

    enum class State(val msg: String) {
        STOPPED("STOPPED"), HOSTING_DESKTOP("[GREEN]HOSTING (D)[]"), HOSTING_GAME("[GREEN]HOSTING (G)[]"), INVALID_SESSION_ID(
            "[RED]INVALID SESSION ID\nPLEASE LOGIN AGAIN[]")
    }

    @Volatile
    var state: State = State.STOPPED

    val messages = ConcurrentLinkedQueue<String>()

    private val parsec = ParsecWrapper()


    // @Volatile
    // private var serverID = -1

    // @Volatile
    // private var desktopMode = false


    val opaque: Pointer = Memory(4)

    val guests = ConcurrentHashMap<Int, String>()


    val parsecTrue = 1.toByte()

    val gst = object : ParsecHostCallbacks.guestStateChange_callback {
        override fun apply(guest: ParsecGuest?, opaque: Pointer?) {
            if (guest != null) {
                val name = String(guest.name)
                val attemptID = String(guest.attemptID)
                log("Parsec", "guestStateChange ${guest.id} $attemptID $name ${guest.state}")
                when (guest.state) {
                    ParsecLibrary.ParsecGuestState.GUEST_CONNECTED -> {
                        guests.put(guest.id, name)
                        val controller = ParsecController(guest.id, name)
                        App.app.parsecControllers.put(guest.id, controller)
                        //ParsecController()
                        messages.add("$name connected")
                    }
                    ParsecLibrary.ParsecGuestState.GUEST_DISCONNECTED -> {
                        guests.remove(guest.id)
                        App.app.parsecControllers.remove(guest.id)
                        messages.add("$name disconnected")
                    }
                    ParsecLibrary.ParsecGuestState.GUEST_CONNECTING -> {
                    }
                    ParsecLibrary.ParsecGuestState.GUEST_FAILED -> {
                        messages.add("$name failed to connect")
                    }
                    ParsecLibrary.ParsecGuestState.GUEST_WAITING -> {
                        val m = Memory(guest.attemptID.size.toLong())
                        m.write(0, guest.attemptID, 0, guest.attemptID.size)
                        ParsecLibrary.ParsecHostAllowGuest(getPointer(), m, parsecTrue)
                    }
                }
            }
        }
    }

    fun getPointer() = parsec.parsecPointer

    val udc = object : ParsecHostCallbacks.userData_callback {
        override fun apply(guest: ParsecGuest?, id: Int, text: Pointer?, opaque: Pointer?) {
            if (guest != null) {
                val name = String(guest.name)
                messages.add("$name: ${text?.getString(0)}")
                log("Parsec",
                    "userdata $id ${text?.getString(0)} ${guest.id} ${guest.attemptID} ${name} ${guest.state}")
            }
        }

    }

    val sic = object : ParsecHostCallbacks.serverID_callback {
        override fun apply(hostID: Int, serverID: Int, opaque: Pointer?) {
            log("Parsec", "serverID $hostID $serverID")
            //this@Parsec.serverID = serverID
            //state = if (desktopMode) State.HOSTING_DESKTOP else State.HOSTING_GAME
            Gdx.app.postRunnable {
                Prefs.NumPref.PARSEC_LAST_SERVER_ID.setNum(serverID)
            }
        }
    }

    val isi = object : ParsecHostCallbacks.invalidSessionID_callback {
        override fun apply(opaque: Pointer?) {
            log("Parsec", "invalidSessionID")
            state = State.INVALID_SESSION_ID
        }
    }

    private val parsecHostCallbacks = ParsecHostCallbacks(gst, udc, sic, isi)

    private val parsecHostConfig = ParsecLibrary.ParsecHostDefaults()

    val plc = object : ParsecLibrary.ParsecLogCallback {
        override fun apply(level: Int, msg: Pointer?, opaque: Pointer?) {
            log("Parsec", "log ${msg?.getString(0)}")
        }
    }


    init {

        parsecHostConfig.encoderFPS=60
        parsecHostConfig.maxGuests=15


        ParsecLibrary.ParsecSetLogCallback(plc, null)

        //            val data = Native.toByteArray(myString);
        //            val pointer = Memory((data.size + 1).toLong());
        //            pointer.write(0, data, 0, data.size);
        //            pointer.setByte(data.size.toLong(), 0);
        //            val b = ByteBuffer.wrap(data)
        //            val parsecHostCallbacks: ParsecHostCallbacks = ParsecHostCallbacks()

        //     ParsecLibrary.ParsecHostGLSubmitFrame(pbr.value, Resources.CONTROLLER1.textureObjectHandle)


        Gdx.app.postRunnable(object : Runnable {
            override fun run() {
                try {
                    if (state == State.HOSTING_GAME) pollInput()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

                Gdx.app.postRunnable(this)
            }
        })

    }

    fun pollInput() {

        for (event in parsec.hostPollInput()) {
            val controller = App.app.parsecControllers[event.guestId]
            when (event) {
                is ParsecWrapper.InputEvent.GamepadButtonEvent -> {
                    controller?.buttonState
                }
            }
            controller?.processMessage(event)
        }




    }


    val nameString = "RetroWar: 8-Bit Party Battle"



    fun hostDesktopStart(id: String) {
        log("Parsec", "hostDesktopstart $id")
        if (state == State.STOPPED || state == State.INVALID_SESSION_ID) {
            val statusCode = parsec.hostStartDesktop(parsecHostConfig,
                nameString,
                id,
                Prefs.NumPref.PARSEC_LAST_SERVER_ID.getNum(),
                parsecHostCallbacks,
                opaque)
            log("parsec", "ParsecHostStar result $statusCode")

            if (statusCode >= 0) {
                state = State.HOSTING_DESKTOP
            }
        }

    }

    fun hostGameStart(id: String) {
        log("Parsec", "hostGamestart $id")
        if (state == State.STOPPED || state == State.INVALID_SESSION_ID) {
            val statusCode = parsec.hostStartGame(parsecHostConfig,
                nameString,
                id,
                Prefs.NumPref.PARSEC_LAST_SERVER_ID.getNum(),
                parsecHostCallbacks,
                opaque)
            log("parsec", "ParsecHostStar result $statusCode")

            if (statusCode >= 0) {
                state = State.HOSTING_GAME
            }

            log("parsec", "ParsecHostStar result $statusCode")
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

    fun status(): String = if (parsec.statusCode < 0) "[RED]ERROR $parsec.statusCode[]" else "${state.msg}"

    fun submitFrame(texture: GLTexture) {
        if (state == State.HOSTING_GAME) {
            parsec.submitFrame(texture.textureObjectHandle)
        }
    }


    //      val status = ParsecHostStatus()
    //      ParsecLibrary.ParsecHostGetStatus(parsec, status)
    //        if(status.invalidSessionID.toUInt().toInt()==0){
    //            return "INVALID SESSION ID"
    //        }


}