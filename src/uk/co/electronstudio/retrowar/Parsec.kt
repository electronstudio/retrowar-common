package uk.co.electronstudio.retrowar

import com.badlogic.gdx.graphics.GLTexture
import com.parsecgaming.parsec.*
import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference
import kong.unirest.JsonNode
import kong.unirest.Unirest
import java.util.concurrent.ConcurrentHashMap


class Parsec {

    enum class State(val msg: String) {
        STOPPED("STOPPED"), STARTING("[YELLOW]STARTING[]"), HOSTING_DESKTOP("[GREEN]HOSTING (D)[]"), HOSTING_GAME("[GREEN]HOSTING (G)[]"), INVALID_SESSION_ID(
                "[RED]INVALID SESSION ID\nPLEASE LOGIN AGAIN[]")
    }

    @Volatile
    var state: State = State.STOPPED


    private val parsecPointer: Pointer?
    @Volatile
    private var serverID = -1
    @Volatile
    private var statusCode = 0
    @Volatile
    private var desktopMode = false
    @Volatile
    var parsecRef: PointerByReference = PointerByReference()

    val guests = ConcurrentHashMap<Int, String>()

    val gst = object : ParsecHostCallbacks.guestStateChange_callback {
        override fun apply(guest: ParsecGuest?, opaque: Pointer?) {
            if (guest != null) {
                val name = String(guest.name)
                val attemptID = String(guest.attemptID)
                log("Parsec", "guestStateChange ${guest?.id} $attemptID $name ${guest?.state}")
                if (guest.state == ParsecLibrary.ParsecGuestState.GUEST_CONNECTED) {
                    guests.put(guest.id, name)
                }
                if (guest.state == ParsecLibrary.ParsecGuestState.GUEST_DISCONNECTED) {
                    guests.remove(guest.id)
                }
            }
        }
    }

    val udc = object : ParsecHostCallbacks.userData_callback {
        override fun apply(guest: ParsecGuest?, id: Int, text: Pointer?, opaque: Pointer?) {
            println("PARSEC USERDATA")
            log("Parsec",
                    "userdata $id ${text?.getString(0)} ${guest?.id} ${guest?.attemptID} ${guest?.name} ${guest?.state}")
        }

    }

    val sic = object : ParsecHostCallbacks.serverID_callback {
        override fun apply(hostID: Int, serverID: Int, opaque: Pointer?) {
            log("Parsec", "serverID $hostID $serverID")
            this@Parsec.serverID = serverID
            state = if (desktopMode) State.HOSTING_DESKTOP else State.HOSTING_GAME
        }
    }

    val isi = object : ParsecHostCallbacks.invalidSessionID_callback {
        override fun apply(opaque: Pointer?) {
            log("Parsec", "invalidSessionID")
            state = State.INVALID_SESSION_ID
        }
    }

    val cbs = ParsecHostCallbacks(gst, udc, sic, isi)

    val phc = ParsecLibrary.ParsecHostDefaults()

    val plc = object : ParsecLibrary.ParsecLogCallback {
        override fun apply(level: Int, msg: Pointer?, opaque: Pointer?) {
            log("Parsec", "log ${msg?.getString(0)}")
        }
    }


    init {


        statusCode = ParsecLibrary.ParsecInit(null, null, parsecRef)


        //println("pbr ${pbr.value}")
        parsecPointer = parsecRef.value




        ParsecLibrary.ParsecSetLogCallback(plc, null)

        //            val data = Native.toByteArray(myString);
        //            val pointer = Memory((data.size + 1).toLong());
        //            pointer.write(0, data, 0, data.size);
        //            pointer.setByte(data.size.toLong(), 0);
        //            val b = ByteBuffer.wrap(data)
        //            val cbs: ParsecHostCallbacks = ParsecHostCallbacks()

        //     ParsecLibrary.ParsecHostGLSubmitFrame(pbr.value, Resources.CONTROLLER1.textureObjectHandle)
    }

    fun pollInput() {
        val guest = ParsecGuest()
        val msg = ParsecMessage()
        while(true) {
            val result = ParsecLibrary.ParsecHostPollInput(parsecPointer, 10, guest, msg)
            if (result.toInt() == 0) {
                break
            }else{
                //log("message received ${msg.type}")
                when(msg.type){
                    ParsecLibrary.ParsecMessageType.MESSAGE_GAMEPAD_BUTTON -> {
                        msg.field1.setType(ParsecGamepadButtonMessage::class.java)
                        val id = msg.field1.gamepadButton.id
                        val button = msg.field1.gamepadButton.button
                        val pressed = msg.field1.gamepadButton.pressed
                        log("button $id $button $pressed")
                    }
                    ParsecLibrary.ParsecMessageType.MESSAGE_GAMEPAD_AXIS -> {
                        msg.field1.setType(ParsecGamepadAxisMessage::class.java)
                        val axis = msg.field1.gamepadAxis.axis
                        val id = msg.field1.gamepadAxis.id
                        val value = msg.field1.gamepadAxis.value
                        log("axis $id $axis $value")
                    }
                    else -> {
                        log("msg type ${msg.type}")
                    }
                }

            }
        }
    }

    fun hostDesktopStart(id: String) {
        log("Parsec", "hostDesktopstart $id")
        if (state == State.STOPPED || state == State.INVALID_SESSION_ID) {
            desktopMode = true
            val m = Memory((id.length + 1).toLong()) // WARNING: assumes ascii-only string
            m.setString(0, id)
            statusCode = ParsecLibrary.ParsecHostStart(parsecPointer,
                    ParsecLibrary.ParsecHostMode.HOST_DESKTOP,
                    phc,
                    cbs,
                    null,
                    null,
                    m,
                    0);
            state = State.STARTING
            log("parsec", "ParsecHostStar result $statusCode")
        }
    }

    fun hostGameStart(id: String) {
        log("Parsec", "hostGamestart $id")
        if (state == State.STOPPED || state == State.INVALID_SESSION_ID) {
            desktopMode = false
            val m = Memory((id.length + 1).toLong()) // WARNING: assumes ascii-only string
            m.setString(0, id)
            statusCode = ParsecLibrary.ParsecHostStart(parsecPointer,
                    ParsecLibrary.ParsecHostMode.HOST_GAME,
                    phc,
                    cbs,
                    null,
                    null,
                    m,
                    1);
            state = State.STARTING
            log("parsec", "ParsecHostStar result $statusCode")
        }
    }

    fun hostDesktopAndWaitForResult(id: String): Boolean {
        hostDesktopStart(id)
        while (true) {
            if (state == State.INVALID_SESSION_ID) {
                return false
            }
            if (state == State.HOSTING_DESKTOP) {
                return true
            }
            Thread.sleep(5)
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
        ParsecLibrary.ParsecHostStop(parsecPointer)
        serverID = -1
        statusCode = 0
        state = State.STOPPED
    }

    fun status(): String = if (statusCode < 0) "[RED]ERROR $statusCode[]" else "${state.msg}"

    fun submitFrame(texture: GLTexture) {
        if (state == State.HOSTING_GAME && parsecPointer != null) {
            ParsecLibrary.ParsecHostGLSubmitFrame(parsecPointer, texture.textureObjectHandle)
        }
    }


    //      val status = ParsecHostStatus()
    //      ParsecLibrary.ParsecHostGetStatus(parsec, status)
    //        if(status.invalidSessionID.toUInt().toInt()==0){
    //            return "INVALID SESSION ID"
    //        }


}