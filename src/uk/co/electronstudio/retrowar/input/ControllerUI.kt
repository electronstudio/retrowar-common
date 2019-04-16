package uk.co.electronstudio.retrowar.input

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import uk.co.electronstudio.retrowar.App
import uk.co.electronstudio.retrowar.PlayerData
import uk.co.electronstudio.retrowar.Resources.Companion.FONT
import uk.co.electronstudio.sdl2gdx.SDL2Controller

class ControllerUI(val controller: SDL2Controller, val x: Float, val y: Float, val tab1:Float, val tab2:Float, val tab3: Float, val tab4: Float) {


    private val statefulController = StatefulController(MappedController(controller))

    var playerData = PlayerData("", com.badlogic.gdx.graphics.Color.RED, com.badlogic.gdx.graphics.Color.BLUE, "", 0)

    var name = ""
    var colour1 = com.badlogic.gdx.graphics.Color.RED
    var colour2 = com.badlogic.gdx.graphics.Color.BLUE

    var index=0

    enum class State {
        SHOWING_PLAYER_NAME, SHOWING_UNASSIGNED, SHOWING_NEW_PLAYER, EDITING, DONE_PLAYER, DONE_UNASSIGNED
    }

    var state = State.SHOWING_UNASSIGNED

    fun player() = App.app.playerData[index]

    fun draw(batch: Batch) {
        if(state==State.DONE_PLAYER || state==State.DONE_UNASSIGNED){
            FONT.color = Color.GRAY
        }else{
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
            State.SHOWING_NEW_PLAYER -> {FONT.draw(batch, "[NEW PLAYER...]", x + tab1, y)}
            State.EDITING -> FONT.draw(batch, "sdffs", x + tab1, y)
        }
        when(state){
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
                    if(index<App.app.playerData.lastIndex){
                        index++
                    }else{
                        state=State.SHOWING_UNASSIGNED
                    }
                }
                State.SHOWING_NEW_PLAYER -> {
                    state = State.SHOWING_UNASSIGNED
                    if(App.app.playerData.isNotEmpty()){
                        state=State.SHOWING_PLAYER_NAME
                        index=0
                    }
                }
                State.EDITING -> {
                    state = State.SHOWING_NEW_PLAYER
                }
                State.DONE_PLAYER -> {}
                State.DONE_UNASSIGNED -> {}
            }
        }
        if (statefulController.isUpButtonJustPressed) {
            when (state) {
                State.SHOWING_UNASSIGNED -> {
                    state = State.SHOWING_NEW_PLAYER
                    if(App.app.playerData.isNotEmpty()){
                        state=State.SHOWING_PLAYER_NAME
                        index=App.app.playerData.lastIndex
                    }
                }
                State.SHOWING_PLAYER_NAME -> {
                    if(index>0){
                        index--
                    }else{
                        state=State.SHOWING_NEW_PLAYER
                    }
                }
                State.SHOWING_NEW_PLAYER -> {
                    state = State.SHOWING_UNASSIGNED
                }
                State.EDITING -> {
                    state = State.SHOWING_NEW_PLAYER
                }
                State.DONE_PLAYER -> {}
                State.DONE_UNASSIGNED -> {}
            }
        }
        if(statefulController.isRightButtonJustPressed || statefulController.isButtonAJustPressed){
            when (state) {
                State.SHOWING_UNASSIGNED -> {
                    state = State.DONE_UNASSIGNED
                }
                State.SHOWING_PLAYER_NAME -> {
                    state = State.DONE_PLAYER
                }
                State.SHOWING_NEW_PLAYER -> {
                    state = State.EDITING
                }
                State.EDITING -> {

                }
                State.DONE_PLAYER -> {
                    state = State.SHOWING_PLAYER_NAME
                }
                State.DONE_UNASSIGNED -> {
                    state = State.SHOWING_UNASSIGNED
                }
            }
        }
        if(statefulController.isLeftButtonJustPressed){
            when (state) {
                State.SHOWING_UNASSIGNED -> {
                }
                State.SHOWING_PLAYER_NAME -> {
                    state = State.EDITING
                }
                State.SHOWING_NEW_PLAYER -> {
                }
                State.EDITING -> {

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
