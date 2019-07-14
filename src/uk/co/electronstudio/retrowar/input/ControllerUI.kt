package uk.co.electronstudio.retrowar.input

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import uk.co.electronstudio.retrowar.*
import uk.co.electronstudio.retrowar.Resources.Companion.FONT
import uk.co.electronstudio.retrowar.Resources.Companion.palette
import uk.co.electronstudio.sdl2gdx.RumbleController
import java.lang.StringBuilder

class ControllerUI(
        val controller: RumbleController,
        val x: Float,
        val y: Float,
        val tab1: Float,
        val tab2: Float,
        val tab3: Float,
        val tab4: Float
) {

    private val statefulController = StatefulController(MappedController(controller))

    //   var playerData = PlayerData("", Color.RED, Color.BLUE, "", 0)

    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!*_"
    var stringBeingEdited = StringBuilder("")
    var cursorPosition = 0
    var charArrayIndex = 0
    var colourBeingEdited1 = Resources.palette[0]
    var colourBeingEdited2 = Resources.palette[0]



    var playerDataArrayIndex = 0

    val flash = Animation<String>(1f / 15f, "[#000000]", "[#ffffff]")
    val flashOnOff = Animation<Int>(1f / 15f, 0, 1)

    enum class State {
        SHOWING_PLAYER_NAME, SHOWING_UNASSIGNED, SHOWING_NEW_PLAYER, EDITING_NAME, EDITING_COLOUR1, EDITING_COLOUR2, DONE_PLAYER, DONE_UNASSIGNED
    }

    var state = State.SHOWING_UNASSIGNED

    fun setNewPlayerDefaults(){
        stringBeingEdited = StringBuilder("A".padEnd(11, '_'))
        colourBeingEdited1 = Resources.palette[2]
        colourBeingEdited2 = Resources.palette[3]
    }

    fun player() = App.app.playerData[playerDataArrayIndex]

    fun isDone() = state == State.DONE_PLAYER || state == State.DONE_UNASSIGNED

    fun init() {
        statefulController.clearEvents()
    }

    fun draw(batch: Batch, time: Float) {
        if (state == State.DONE_PLAYER || state == State.DONE_UNASSIGNED) {
            FONT.color = Color.GRAY
        } else {
            FONT.color = Color.WHITE
        }
        FONT.draw(batch, controller.name.substringAfter("SDL GameController ").take(19), 0f, y)
        when (state) {
            State.SHOWING_UNASSIGNED, State.DONE_UNASSIGNED -> FONT.draw(batch, "[UNASSIGNED]", x + tab1, y)
            State.SHOWING_PLAYER_NAME, State.DONE_PLAYER -> {
                FONT.draw(batch, "${player().name}", x + tab1, y)
                FONT.draw(batch, "[#${player().color}];;;;;;[]", x + tab2, y)
                FONT.draw(batch, "[#${player().color2}];;;;;;[]", x + tab3, y)
            }
            State.SHOWING_NEW_PLAYER -> {
                FONT.draw(batch, "[NEW PLAYER...]", x + tab1, y)
            }
            State.EDITING_NAME -> {
                val start = stringBeingEdited.toString().substring(0, cursorPosition)
                val middle = stringBeingEdited.toString().substring(cursorPosition, cursorPosition + 1)
                val end = stringBeingEdited.toString().substring(cursorPosition + 1, stringBeingEdited.length)
                val s = "$start${flash.getKeyFrame(time, true)}$middle[]$end"
                FONT.draw(batch, s, x + tab1, y)
                FONT.draw(batch, "[#$colourBeingEdited1];;;;;;[]", x + tab2, y)
                FONT.draw(batch, "[#$colourBeingEdited2];;;;;;[]", x + tab3, y)
                // println(s)
            }
            State.EDITING_COLOUR1 -> {
                FONT.draw(batch, stringBeingEdited, x + tab1, y)
                val s =
                    "${flash.getKeyFrame(time, true)}>[#$colourBeingEdited1];;;;[]${flash.getKeyFrame(time, true)}<[]"
                FONT.draw(batch, s, x + tab2, y)

                FONT.draw(batch, "[#$colourBeingEdited2];;;;;;[]", x + tab3, y)
                // println(s)
            }
            State.EDITING_COLOUR2 -> {
                FONT.draw(batch, stringBeingEdited, x + tab1, y)

                FONT.draw(batch, "[#$colourBeingEdited1];;;;;;[]", x + tab2, y)
                val s =
                    "${flash.getKeyFrame(time, true)}>[#$colourBeingEdited2];;;;[]${flash.getKeyFrame(time, true)}<[]"
                FONT.draw(batch, s, x + tab3, y)

                // println(s)
            }
        }
        if (state == State.DONE_PLAYER || state == State.DONE_UNASSIGNED) {
            FONT.draw(batch, "READY!", x + tab4, y)
        }
    }

    var repeatTimerDown = 0f
    var repeatTimerUp = 0f

    val INITIAL_REPEAT_DELAY = 0.7f
    val REPEAT_INTERVAL = 0.1f

    fun doInput(delta: Float) {

        if(statefulController.mappedController.LStickVerticalAxis() > 0.6f || statefulController.mappedController.dPadDown()){
            repeatTimerDown+=delta
        }else{
            repeatTimerDown=-0f
        }


        if(statefulController.mappedController.LStickVerticalAxis() < -0.6f || statefulController.mappedController.dPadUp()){
            repeatTimerUp+=delta
        }else{
            repeatTimerUp=-0f
        }


        if (statefulController.isDownButtonJustPressed || repeatTimerDown > INITIAL_REPEAT_DELAY ) {
            repeatTimerDown-=REPEAT_INTERVAL
            when (state) {
                State.SHOWING_UNASSIGNED -> {
                    state = State.SHOWING_NEW_PLAYER
                }
                State.SHOWING_PLAYER_NAME -> {
                    if (playerDataArrayIndex < App.app.playerData.lastIndex) {
                        playerDataArrayIndex++
                    } else {
                        state = State.SHOWING_UNASSIGNED
                    }
                }
                State.SHOWING_NEW_PLAYER -> {
                    state = State.SHOWING_UNASSIGNED
                    if (App.app.playerData.isNotEmpty()) {
                        state = State.SHOWING_PLAYER_NAME
                        playerDataArrayIndex = 0
                    }
                }
                State.EDITING_NAME -> {
                    if (cursorPosition < stringBeingEdited.length) {
                        charArrayIndex++
                        if (charArrayIndex > chars.length - 1) {
                            charArrayIndex = 0
                        }
                        stringBeingEdited[cursorPosition] = chars[charArrayIndex]
                    }
                }
                State.EDITING_COLOUR1 -> {
                    var i = palette.indexOf(colourBeingEdited1)
                    i--
                    if (i < 0) {
                        i = palette.size - 1
                    }
                    colourBeingEdited1 = palette[i]
                }
                State.EDITING_COLOUR2 -> {
                    var i = palette.indexOf(colourBeingEdited2)
                    i--
                    if (i < 0) {
                        i = palette.size - 1
                    }
                    colourBeingEdited2 = palette[i]
                }
                State.DONE_PLAYER -> {
                }
                State.DONE_UNASSIGNED -> {
                }
            }
        }
        if (statefulController.isUpButtonJustPressed || repeatTimerUp > INITIAL_REPEAT_DELAY ) {
            repeatTimerUp-=REPEAT_INTERVAL
            when (state) {
                State.SHOWING_UNASSIGNED -> {
                    state = State.SHOWING_NEW_PLAYER
                    if (App.app.playerData.isNotEmpty()) {
                        state = State.SHOWING_PLAYER_NAME
                        playerDataArrayIndex = App.app.playerData.lastIndex
                    }
                }
                State.SHOWING_PLAYER_NAME -> {
                    if (playerDataArrayIndex > 0) {
                        playerDataArrayIndex--
                    } else {
                        state = State.SHOWING_NEW_PLAYER
                    }
                }
                State.SHOWING_NEW_PLAYER -> {
                    state = State.SHOWING_UNASSIGNED
                }
                State.EDITING_NAME -> {
                    if (cursorPosition < stringBeingEdited.length) {
                        charArrayIndex--
                        if (charArrayIndex < 0) {
                            charArrayIndex = chars.length - 1
                        }
                        stringBeingEdited[cursorPosition] = chars[charArrayIndex]
                    }
                }
                State.EDITING_COLOUR1 -> {
                    var i = palette.indexOf(colourBeingEdited1)
                    i++
                    if (i > palette.size - 1) {
                        i = 0
                    }
                    colourBeingEdited1 = palette[i]
                }
                State.EDITING_COLOUR2 -> {
                    var i = palette.indexOf(colourBeingEdited2)
                    i++
                    if (i > palette.size - 1) {
                        i = 0
                    }
                    colourBeingEdited2 = palette[i]
                }
                State.DONE_PLAYER -> {
                }
                State.DONE_UNASSIGNED -> {
                }
            }
        }
        if (statefulController.isRightButtonJustPressed || statefulController.isButtonAJustPressed || statefulController.isButtonBJustPressed || statefulController.isButtonXJustPressed || statefulController.isButtonYJustPressed) {
            log("ControlluerUI", "button press")
            when (state) {
                State.SHOWING_UNASSIGNED -> {
                    state = State.DONE_UNASSIGNED
                }
                State.SHOWING_PLAYER_NAME -> {
                    state = State.DONE_PLAYER
                }
                State.SHOWING_NEW_PLAYER -> {
                    //  editName.s
                    cursorPosition = 0
                    setNewPlayerDefaults()
                    state = State.EDITING_NAME
                }
                State.EDITING_NAME -> {
                    if (cursorPosition < stringBeingEdited.length - 1) {
                        cursorPosition++
                        stringBeingEdited[cursorPosition] = chars[charArrayIndex]
                    } else {
                        state = State.EDITING_COLOUR1
                    }
                }
                State.EDITING_COLOUR1 -> {
                    state = State.EDITING_COLOUR2
                }
                State.EDITING_COLOUR2 -> {
                    val playerData = PlayerData(stringBeingEdited.toString().replace('_', ' ').trim(),
                        colourBeingEdited1,
                        colourBeingEdited2,
                        controller.name,
                        0)
                    val playerWithMatchingName = App.app.playerData.find { it.name == playerData.name }
                    if(playerWithMatchingName==null) {
                        App.app.playerData.add(playerData)
                        playerDataArrayIndex = App.app.playerData.lastIndex
                    }else{
                        playerWithMatchingName.color=colourBeingEdited1
                        playerWithMatchingName.color2=colourBeingEdited2
                        playerDataArrayIndex = App.app.playerData.indexOf(playerWithMatchingName)
                    }

                    state = State.DONE_PLAYER
                }
                State.DONE_PLAYER -> {
                    state = State.SHOWING_PLAYER_NAME
                }
                State.DONE_UNASSIGNED -> {
                    state = State.SHOWING_UNASSIGNED
                }
            }
        }
        if (statefulController.isLeftButtonJustPressed) {
            when (state) {
                State.SHOWING_UNASSIGNED -> {
                }
                State.SHOWING_PLAYER_NAME -> {
                     stringBeingEdited = StringBuilder(player().name.padEnd(11, '_'))
                     colourBeingEdited1 = player().color
                     colourBeingEdited2 = player().color2
                     cursorPosition=stringBeingEdited.length-1
                     charArrayIndex = chars.indexOf(stringBeingEdited[cursorPosition])
                     state = State.EDITING_COLOUR2
                }
                State.SHOWING_NEW_PLAYER -> {
                }
                State.EDITING_NAME -> {
                    if (cursorPosition > 0) {
                        cursorPosition--
                        charArrayIndex = chars.indexOf(stringBeingEdited[cursorPosition])
                    }
                }
                State.EDITING_COLOUR1 -> {
                    state = State.EDITING_NAME
                }
                State.EDITING_COLOUR2 -> {
                    state = State.EDITING_COLOUR1
                }
                State.DONE_PLAYER -> {
                    state = State.SHOWING_PLAYER_NAME
                }
                State.DONE_UNASSIGNED -> {
                    state = State.SHOWING_UNASSIGNED
                }
            }
        }
    }
}
