package uk.co.electronstudio.retrowar.input

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import uk.co.electronstudio.retrowar.App
import uk.co.electronstudio.retrowar.PlayerData
import uk.co.electronstudio.retrowar.Resources
import uk.co.electronstudio.retrowar.Resources.Companion.FONT
import uk.co.electronstudio.retrowar.Resources.Companion.palette
import uk.co.electronstudio.sdl2gdx.SDL2Controller
import java.lang.StringBuilder

class ControllerUI(val controller: SDL2Controller, val x: Float, val y: Float, val tab1: Float, val tab2: Float,
                   val tab3: Float, val tab4: Float) {


    private val statefulController = StatefulController(MappedController(controller))

    var playerData = PlayerData("", com.badlogic.gdx.graphics.Color.RED, com.badlogic.gdx.graphics.Color.BLUE, "", 0)


    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXY0123456789!*_"
    var editName = StringBuilder("A____________")
    var cursor = 0
    var charIndex = 0
    var editColour1 = Resources.palette[2]
    var editColour2 = Resources.palette[3]

    var index = 0

    val flash = Animation<String>(1f / 15f, "[#000000]", "[#ffffff]")
    val flashOnOff = Animation<Int>(1f / 15f, 0, 1)

    enum class State {
        SHOWING_PLAYER_NAME, SHOWING_UNASSIGNED, SHOWING_NEW_PLAYER, EDITING_NAME, EDITING_COLOUR1, EDITING_COLOUR2, DONE_PLAYER, DONE_UNASSIGNED
    }

    var state = State.SHOWING_UNASSIGNED

    fun player() = App.app.playerData[index]

    fun draw(batch: Batch, time: Float) {
        if (state == State.DONE_PLAYER || state == State.DONE_UNASSIGNED) {
            FONT.color = Color.GRAY
        } else {
            FONT.color = Color.WHITE
        }
        FONT.draw(batch, controller.name.substringAfter("SDL GameController "), 0f, y)
        when (state) {
            State.SHOWING_UNASSIGNED, State.DONE_UNASSIGNED -> FONT.draw(batch, "[UNASSIGNED]", x + tab1, y)
            State.SHOWING_PLAYER_NAME, State.DONE_PLAYER -> {
                FONT.draw(batch, "${player().name}", x + tab1, y)
                FONT.draw(batch, "[#${player().color.toString()}];;;;;;[]", x + tab2, y)
                FONT.draw(batch, "[#${player().color2.toString()}];;;;;;[]", x + tab3, y)
            }
            State.SHOWING_NEW_PLAYER -> {
                FONT.draw(batch, "[NEW PLAYER...]", x + tab1, y)
            }
            State.EDITING_NAME -> {
                val start = editName.toString().substring(0, cursor)
                val middle = editName.toString().substring(cursor, cursor + 1)
                val end = editName.toString().substring(cursor + 1, editName.length)
                val s = "$start${flash.getKeyFrame(time, true)}$middle[]$end"
                FONT.draw(batch, s, x + tab1, y)
                FONT.draw(batch, "[#${editColour1.toString()}];;;;;;[]", x + tab2, y)
                FONT.draw(batch, "[#${editColour2.toString()}];;;;;;[]", x + tab3, y)
                // println(s)
            }
            State.EDITING_COLOUR1 -> {
                FONT.draw(batch, editName, x + tab1, y)
                val s = "${flash.getKeyFrame(time, true)}>[#${editColour1.toString()}];;;;[]${flash.getKeyFrame(time, true)}<[]"
                FONT.draw(batch, s, x + tab2, y)

                FONT.draw(batch, "[#${editColour2.toString()}];;;;;;[]", x + tab3, y)
                // println(s)
            }
            State.EDITING_COLOUR2 -> {
                FONT.draw(batch, editName, x + tab1, y)

                FONT.draw(batch, "[#${editColour1.toString()}];;;;;;[]", x + tab2, y)
                val s = "${flash.getKeyFrame(time, true)}>[#${editColour2.toString()}];;;;[]${flash.getKeyFrame(time, true)}<[]"
                FONT.draw(batch, s, x + tab3, y)

                // println(s)
            }
        }
        when (state) {
            State.DONE_PLAYER, State.DONE_UNASSIGNED -> {
                FONT.draw(batch, "READY!", x + tab4, y)
            }
        }
        if (statefulController.isDownButtonJustPressed) {
            when (state) {
                State.SHOWING_UNASSIGNED -> {
                    state = State.SHOWING_NEW_PLAYER
                }
                State.SHOWING_PLAYER_NAME -> {
                    if (index < App.app.playerData.lastIndex) {
                        index++
                    } else {
                        state = State.SHOWING_UNASSIGNED
                    }
                }
                State.SHOWING_NEW_PLAYER -> {
                    state = State.SHOWING_UNASSIGNED
                    if (App.app.playerData.isNotEmpty()) {
                        state = State.SHOWING_PLAYER_NAME
                        index = 0
                    }
                }
                State.EDITING_NAME -> {
                    if (cursor < editName.length) {
                        charIndex++
                        if (charIndex > chars.length - 1) {
                            charIndex = 0
                        }
                        editName[cursor] = chars[charIndex]
                    }
                }
                State.EDITING_COLOUR1 -> {
                    var i = Resources.palette.indexOf(editColour1)
                    i--
                    if (i < 0) {
                        i = Resources.palette.size - 1
                    }
                    editColour1 = palette[i]
                }
                State.EDITING_COLOUR2 -> {
                    var i = Resources.palette.indexOf(editColour2)
                    i--
                    if (i < 0) {
                        i = Resources.palette.size - 1
                    }
                    editColour2 = palette[i]
                }
                State.DONE_PLAYER -> {
                }
                State.DONE_UNASSIGNED -> {
                }
            }
        }
        if (statefulController.isUpButtonJustPressed) {
            when (state) {
                State.SHOWING_UNASSIGNED -> {
                    state = State.SHOWING_NEW_PLAYER
                    if (App.app.playerData.isNotEmpty()) {
                        state = State.SHOWING_PLAYER_NAME
                        index = App.app.playerData.lastIndex
                    }
                }
                State.SHOWING_PLAYER_NAME -> {
                    if (index > 0) {
                        index--
                    } else {
                        state = State.SHOWING_NEW_PLAYER
                    }
                }
                State.SHOWING_NEW_PLAYER -> {
                    state = State.SHOWING_UNASSIGNED
                }
                State.EDITING_NAME -> {
                    if (cursor < editName.length) {
                        charIndex--
                        if (charIndex < 0) {
                            charIndex = chars.length - 1
                        }
                        editName[cursor] = chars[charIndex]
                    }
                }
                State.EDITING_COLOUR1 -> {
                    var i = Resources.palette.indexOf(editColour1)
                    i++
                    if (i > Resources.palette.size - 1) {
                        i = 0
                    }
                    editColour1 = palette[i]
                }
                State.EDITING_COLOUR2 -> {
                    var i = Resources.palette.indexOf(editColour2)
                    i++
                    if (i > Resources.palette.size - 1) {
                        i = 0
                    }
                    editColour2 = palette[i]
                }
                State.DONE_PLAYER -> {
                }
                State.DONE_UNASSIGNED -> {
                }
            }
        }
        if (statefulController.isRightButtonJustPressed || statefulController.isButtonAJustPressed) {
            when (state) {
                State.SHOWING_UNASSIGNED -> {
                    state = State.DONE_UNASSIGNED
                }
                State.SHOWING_PLAYER_NAME -> {
                    state = State.DONE_PLAYER
                }
                State.SHOWING_NEW_PLAYER -> {
                    //  editName.s
                    cursor = 0
                    state = State.EDITING_NAME
                }
                State.EDITING_NAME -> {
                    if (cursor < editName.length - 1) {
                        cursor++ //fixme repeat previous character?
                    } else {
                        state = State.EDITING_COLOUR1
                    }
                }
                State.EDITING_COLOUR1 -> {
                    state = State.EDITING_COLOUR2
                }
                State.EDITING_COLOUR2 -> {
                    App.app.playerData.add(PlayerData(editName.toString().trim(),  //fixme remove underscores
                        editColour1,
                        editColour2,
                        controller.name,
                        0))
                    index = App.app.playerData.lastIndex
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
                    state = State.EDITING_COLOUR2
                }
                State.SHOWING_NEW_PLAYER -> {
                }
                State.EDITING_NAME -> {
                    if (cursor > 0) {
                        cursor--
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
