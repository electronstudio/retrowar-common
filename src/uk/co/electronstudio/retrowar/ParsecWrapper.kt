package uk.co.electronstudio.retrowar

import com.parsecgaming.parsec.ParsecGamepadAxisMessage
import com.parsecgaming.parsec.ParsecGamepadButtonMessage
import com.parsecgaming.parsec.ParsecGuest
import com.parsecgaming.parsec.ParsecLibrary
import com.parsecgaming.parsec.ParsecMessage
import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference


class ParsecWrapper{
    val parsecPointer: Pointer?

    val parsecRef: PointerByReference = PointerByReference()
    @Volatile
    var statusCode = 0

    init {
        statusCode = ParsecLibrary.ParsecInit(parsecConfig, null, parsecRef)



        parsecPointer = parsecRef.value

    }

    val guest = ParsecGuest()
    val msg = ParsecMessage()

    fun hostPollInput(): List<InputEvent>{
        val events = arrayListOf<InputEvent>()
        while (ParsecLibrary.ParsecHostPollInput(parsecPointer, 0, guest, msg).toInt() == 1) {
            events.add(InputEvent(guest, msg))
        }
        return events
    }



    abstract class InputEvent private constructor(){
        abstract val guestId: Int
        companion object {
            @JvmStatic
            fun build(guest: ParsecGuest, msg: ParsecMessage): InputEvent {
                when (msg.type) {
                    ParsecLibrary.ParsecMessageType.MESSAGE_GAMEPAD_BUTTON -> {
                        msg.field1.setType(ParsecGamepadButtonMessage::class.java)
                        val id = msg.field1.gamepadButton.id
                        val button = msg.field1.gamepadButton.button
                        val pressed = msg.field1.gamepadButton.pressed
                        return ButtonInputEvent(guest.id, id, button, pressed.toInt() == 1)
                    }
                    ParsecLibrary.ParsecMessageType.MESSAGE_GAMEPAD_AXIS -> {
                        msg.field1.setType(ParsecGamepadAxisMessage::class.java)
                        val axis = msg.field1.gamepadAxis.axis
                        val id = msg.field1.gamepadAxis.id
                        val value = msg.field1.gamepadAxis.value
                        setAxis(axis, value)
                        log("axis $id $axis $value")
                    }
                    else -> {

                        log("msg type ${msg.type}")
                    }
                }
                return ButtonInputEvent(guest, msg)
            }
        }

        data class ButtonInputEvent(override val guestId: Int, val id: Int, val button: Int, val pressed: Boolean): InputEvent(){}
        data class
    }



}