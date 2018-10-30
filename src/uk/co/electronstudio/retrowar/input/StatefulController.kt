package uk.co.electronstudio.retrowar.input

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.controllers.PovDirection

/**
 * Gets state of wrapped controller using events, then provides this state through an
 * api that can be polled
 * Useful for menus (and simple games where you don't want to miss an input ?)
 */
internal class StatefulController(
    val c: MappedController
) : ControllerAdapter() {

    var startPressed =
        false
    var upPressed =
        false
    var downPressed =
        false
    var leftPressed =
        false
    var rightPressed =
        false
    var APressed =
        false
    var BPressed =
        false

    var horCentered =
        true
    var vertCentered =
        true

    val THRESHOLD =
        0.3
    val THRESHOLD_H =
        0.6

    init {
        c.controller.addListener(
            this
        )
    }

    fun clearEvents() {
        startPressed =
                false
        upPressed =
                false
        downPressed =
                false
        leftPressed =
                false
        rightPressed =
                false
        APressed =
                false
        BPressed =
                false
        horCentered =
                true
        vertCentered =
                true
    }

    val isUpButtonJustPressed: Boolean
        get() {
            val t =
                upPressed
            upPressed =
                    false
            return t
        }

    val isDownButtonJustPressed: Boolean
        get() {
            val t =
                downPressed
            downPressed =
                    false
            return t
        }

    val isLeftButtonJustPressed: Boolean
        get() {
            val t =
                leftPressed
            leftPressed =
                    false
            return t
        }

    val isRightButtonJustPressed: Boolean
        get() {
            val t =
                rightPressed
            rightPressed =
                    false
            return t
        }

    val isButtonAJustPressed: Boolean
        get() {
            //   log("stateful $this pressed button")
            val t =
                APressed
            APressed =
                    false
            return t
        }
    val isButtonBJustPressed: Boolean
        get() {
            val t =
                BPressed
            BPressed =
                    false
            return t
        }

    val isStartButtonJustPressed: Boolean
        get() {
            val t =
                startPressed
            startPressed =
                    false
            return t
        }

    override fun buttonDown(
        controller: Controller?,
        buttonIndex: Int
    ): Boolean {

        when (buttonIndex) {
            c.START -> startPressed =
                    true
            c.A -> APressed =
                    true
            c.B -> BPressed =
                    true
            c.DPAD_UP -> upPressed =
                    true
            c.DPAD_DOWN -> downPressed =
                    true
            c.DPAD_LEFT -> leftPressed =
                    true
            c.DPAD_RIGHT -> rightPressed =
                    true
        }

        return true
    }

    override fun povMoved(
        controller: Controller?,
        povIndex: Int,
        value: PovDirection?
    ): Boolean {

        when (value) {
            PovDirection.north -> upPressed =
                    true
            PovDirection.south -> downPressed =
                    true
            PovDirection.west -> leftPressed =
                    true
            PovDirection.east -> rightPressed =
                    true
            PovDirection.center -> {
            }
            PovDirection.northEast -> {
            }
            PovDirection.northWest -> {
            }
            PovDirection.southEast -> {
            }
            PovDirection.southWest -> {
            }
        }

        return true
    }

    override fun axisMoved(
        controller: Controller?,
        axisIndex: Int,
        value: Float
    ): Boolean {
        if (axisIndex == c.L_STICK_HORIZONTAL_AXIS) {
            if (value > THRESHOLD_H && horCentered) {
                rightPressed =
                        true
                horCentered =
                        false
            } else if (value < -THRESHOLD_H && horCentered) {
                leftPressed =
                        true
                horCentered =
                        false
            } else if (value < 0.1 && value > -0.1) {
                horCentered =
                        true
            }
        } else if (axisIndex == c.L_STICK_VERTICAL_AXIS) {
            if (value > THRESHOLD && vertCentered) {
                downPressed =
                        true
                vertCentered =
                        false
            } else if (value < -THRESHOLD && vertCentered) {
                upPressed =
                        true
                vertCentered =
                        false
            } else if (value < 0.1 && value > -0.1) {
                vertCentered =
                        true
            }
        }

        return true
    }
}

/*
        * move this to MAppedController.  possibly make a named controlleradapter class, or use mappedcontroller to implement
        it, whatever.  DONE
        * make isJustPressed behave like the keyboard one, only store the data for one frame TODO NOT SURE WONT MISS STUFF
           so anyone who wants to use these events will have to call a poll method first anyway
              so we could do it all by polling, the only reason for events is that polling the mouse was found to miss clicks
           no??? we can assume each bool is read once per frame, then no need for poll method??? but then we can't know
           the state on the previous frame to know if its a press or a hold
           also if it doesnt get read by a screen but does get set then event stayed until another screen reads it

        for controllers, using the gdx event api still doesnt get us press/depress events on sticks
        the old way of having a delay in the menu after an input received treats keyboard and controller the same
        when actually we may want key-repeat behaviour to be different.  could scale repeats with how far stick is
        pushed for instance.

        * can we do poll in app render so its always done?  i think so. TODO NOT SURE

        polling vs events
        if you want controller stuff to be entirely
        poll based we could do it here too, but since you seem to be able to stack as many listeners as you want
        on a controller i dont see a downside of using them.  (with key/mouse listener you only get one, unless you
        deliberately using a multiplex one, but then if you forgot on any other screen and overwrite it you lose your listeners)
        listeners are kind of like 'comefrom' though.  if you forget you created them you have code executing that you cant discover
        just from reading the main loop.  if they are created when app starts and they dont affect code in other files that
        seems ok.  but game session adds and removes listeners during app lifetime.

        * maybe we could get rid of listeners in gamesession though and just query an isAnyButtonJustPressed for each controller?

        * remember also to test splashscreens DONE
  */
