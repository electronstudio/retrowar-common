package uk.co.electronstudio.retrowar

import com.parsecgaming.parsec.ParsecGuest
import com.parsecgaming.parsec.ParsecHostCallbacks
import com.parsecgaming.parsec.ParsecLibrary
import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference
import kong.unirest.JsonNode
import kong.unirest.Unirest


class Parsec {

    val parsec: Pointer?
    var serverID = -1
    var invalidSessionID = false
    var errorCode = 0
    var desktopMode=false

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
        }
    }

    val isi = object : ParsecHostCallbacks.invalidSessionID_callback {
        override fun apply(opaque: Pointer?) {
            log("Parsec", "invalidSessionID")
            this@Parsec.invalidSessionID = true
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
        //val ps = ParsecLibrary.Parsec()
        val pbr = PointerByReference()
        val ok = ParsecLibrary.ParsecInit(null, null, pbr)


        println("pbr ${pbr.value}")
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
        desktopMode=true
        val m = Memory((id.length + 1).toLong()) // WARNING: assumes ascii-only string
        m.setString(0, id)
        errorCode = ParsecLibrary.ParsecHostStart(parsec,
            ParsecLibrary.ParsecHostMode.HOST_DESKTOP,
            phc,
            cbs,
            null,
            null,
            m,
            0);
        log("parsec", "ParsecHostStar result $errorCode")
    }

    fun hostGameStart(id: String) {
        log("Parsec", "hostGamestart $id")
        desktopMode=false
        val m = Memory((id.length + 1).toLong()) // WARNING: assumes ascii-only string
        m.setString(0, id)
        errorCode = ParsecLibrary.ParsecHostStart(parsec,
                ParsecLibrary.ParsecHostMode.HOST_GAME,
                phc,
                cbs,
                null,
                null,
                m,
                0);
        log("parsec", "ParsecHostStar result $errorCode")
    }

    fun hostDesktopAndWaitForResult(id: String): Boolean {
        hostDesktopStart(id)
        while (true) {
            if (invalidSessionID) {
                return false
            }
            if (serverID >= 0) {
                return true
            }
            Thread.sleep(5)
        }
    }

    fun login(email: String, password: String, tfa: String) {
        val text = "{\"email\": \"$email\", \"password\": \"$password\", \"tfa\": \"$tfa\"}"
        Unirest.post("https://parsecgaming.com/v1/auth/").header("Content-Type", "application/json")
            .body(text).asJsonAsync({ response ->
                val code = response.getStatus()
                val body: JsonNode = response.getBody()
                loginResult=body.toString()
                    log("Parec", "login result $code $body")
            })
    }

    fun loginSync(email: String, password: String, tfa: String) : Pair<Int, String>{
        val text = "{\"email\": \"$email\", \"password\": \"$password\", \"tfa\": \"$tfa\"}"
        val request = Unirest.post("https://parsecgaming.com/v1/auth/").header("Content-Type", "application/json")
            .body(text).asJson()
        loginResult=request.body.toString()
        return Pair(request.status, loginResult)
    }


    fun stop() {
        ParsecLibrary.ParsecHostStop(parsec)
        serverID = -1
        invalidSessionID = false
        errorCode = 0
    }

    fun status(): String {

        //      val status = ParsecHostStatus()
        //      ParsecLibrary.ParsecHostGetStatus(parsec, status)
        //        if(status.invalidSessionID.toUInt().toInt()==0){
        //            return "INVALID SESSION ID"
        //        }
        if (invalidSessionID) {
            return "[RED]INVALID SESSION ID\nPLEASE LOGIN AGAIN[]"
        } else if (serverID >= 0) {
            return "[GREEN]HOSTING[]"
        } else if (errorCode != 0) {
            return "[RED]ERROR $errorCode[]"
        } else {
            return "STOPPED"
        }

    }

    var loginResult = "(We do not retain your password after login.)"


}