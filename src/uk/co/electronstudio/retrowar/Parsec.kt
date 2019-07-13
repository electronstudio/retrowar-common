package uk.co.electronstudio.retrowar

import com.badlogic.gdx.graphics.GLTexture
import com.parsecgaming.parsec.ParsecGuest
import com.parsecgaming.parsec.ParsecHostCallbacks
import com.parsecgaming.parsec.ParsecLibrary
import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference
import kong.unirest.JsonNode
import kong.unirest.Unirest


class Parsec {

    enum class State(val msg: String) {
        STOPPED("STOPPED"), STARTING("[YELLOW]STARTING[]"), HOSTING_DESKTOP("[GREEN]HOSTING (D)[]"), HOSTING_GAME("[GREEN]HOSTING (G)[]"), INVALID_SESSION_ID(
            "[RED]INVALID SESSION ID\nPLEASE LOGIN AGAIN[]")
    }

    var state: State = State.STOPPED

    private val parsec: Pointer?
    private var serverID = -1
    private var statusCode = 0
    private var desktopMode = false

    val gst = object : ParsecHostCallbacks.guestStateChange_callback {
        override fun apply(guest: ParsecGuest?, opaque: Pointer?) {
            val name = if (guest != null) String(guest.name) else null
            val attemptID = if (guest != null) String(guest.attemptID) else null
            log("Parsec", "guestStateChange ${guest?.id} $attemptID $name ${guest?.state}")
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

        val pbr = PointerByReference()
        statusCode = ParsecLibrary.ParsecInit(null, null, pbr)


        //println("pbr ${pbr.value}")
        parsec = pbr.value




        ParsecLibrary.ParsecSetLogCallback(plc, null)

        //            val data = Native.toByteArray(myString);
        //            val pointer = Memory((data.size + 1).toLong());
        //            pointer.write(0, data, 0, data.size);
        //            pointer.setByte(data.size.toLong(), 0);
        //            val b = ByteBuffer.wrap(data)
        //            val cbs: ParsecHostCallbacks = ParsecHostCallbacks()

        //     ParsecLibrary.ParsecHostGLSubmitFrame(pbr.value, Resources.CONTROLLER1.textureObjectHandle)
    }

    fun hostDesktopStart(id: String) {
        log("Parsec", "hostDesktopstart $id")
        if(state==State.STOPPED || state==State.INVALID_SESSION_ID) {
            desktopMode = true
            val m = Memory((id.length + 1).toLong()) // WARNING: assumes ascii-only string
            m.setString(0, id)
            statusCode = ParsecLibrary.ParsecHostStart(parsec,
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
        if(state==State.STOPPED || state==State.INVALID_SESSION_ID) {
            desktopMode = false
            val m = Memory((id.length + 1).toLong()) // WARNING: assumes ascii-only string
            m.setString(0, id)
            statusCode = ParsecLibrary.ParsecHostStart(parsec,
                ParsecLibrary.ParsecHostMode.HOST_GAME,
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
        ParsecLibrary.ParsecHostStop(parsec)
        serverID = -1
        statusCode = 0
        state = State.STOPPED
    }

    fun status(): String = if (statusCode < 0) "[RED]ERROR $statusCode[]" else "${state.msg}"

    fun submitFrame(texture: GLTexture) {
        ParsecLibrary.ParsecHostGLSubmitFrame(parsec, texture.textureObjectHandle)
    }


    //      val status = ParsecHostStatus()
    //      ParsecLibrary.ParsecHostGetStatus(parsec, status)
    //        if(status.invalidSessionID.toUInt().toInt()==0){
    //            return "INVALID SESSION ID"
    //        }


}