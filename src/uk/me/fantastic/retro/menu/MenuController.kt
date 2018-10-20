package uk.me.fantastic.retro.menu

import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys.BACK
import com.badlogic.gdx.Input.Keys.DOWN
import com.badlogic.gdx.Input.Keys.ENTER
import com.badlogic.gdx.Input.Keys.ESCAPE
import com.badlogic.gdx.Input.Keys.LEFT
import com.badlogic.gdx.Input.Keys.RIGHT
import com.badlogic.gdx.Input.Keys.SPACE
import com.badlogic.gdx.Input.Keys.UP
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Align
import uk.me.fantastic.retro.App.Companion.app
import uk.me.fantastic.retro.Resources
import uk.me.fantastic.retro.createDefaultShapeShader
import uk.me.fantastic.retro.drawBox
import uk.me.fantastic.retro.log
import java.util.Stack

/**
 * Wraps a Menu and changes menu selections based on input
 */

class MenuController(
    val rootMenu: Menu,
    val WIDTH: Float,
    val HEIGHT: Float,
    val font: BitmapFont = Resources.FONT_CLEAR,
    val x: Float = 0f,
    val y: Float
) {

    val menus = Stack<Menu>()

    internal var glyphLayout = GlyphLayout()

    var count = 0

    // val sequence = arrayOf("RED", "PURPLE", "BLUE", "CYAN", "GREEN", "YELLOW")
    val sequence = arrayOf("RED", "RED", "PURPLE", "BLUE", "BLUE", "CYAN", "GREEN", "GREEN", "YELLOW")

    var flash = ""

    internal var shape = ShapeRenderer(5000, createDefaultShapeShader())

    init {
        font.data.markupEnabled = true
        menus.push(rootMenu)
    }

    fun draw(batch: Batch) {

        batch.end()

        // updateaGlyph("RED")

        shape.projectionMatrix = batch.projectionMatrix

        val MARGIN = 6
        val SHADOW_OFFSET = 5

        drawBox(MARGIN, SHADOW_OFFSET, shape, glyphLayout.width, glyphLayout.height, y, WIDTH)

        batch.begin()
        // fontClear.draw(batch, glyphLayout, 0f, y)
        drawFlashing(batch)
        //   batch.end()
    }

    fun drawFlashing(batch: Batch) {
//        batch.begin()
        flash = sequence[count++ % sequence.size]
        updateaGlyph(flash)
        font.draw(batch, glyphLayout, x, y)
        //   batch.end()
    }

    fun updateaGlyph(color: String) {
        glyphLayout.setText(font, menus.peek().getText(color), Color.WHITE, WIDTH, Align.center, true)
    }

    fun select() {
        log("select")
        val s = menus.peek().getSelected()
        when (s) {
            is StringPrefMenuItem -> {
                s.stringPref.setString("")
                menus.peek().editing = s
            }
            is SubMenuItem -> menus.push(s.subMenu)
            is BackMenuItem -> doBack()
            else -> s.doAction() // fixme theres logic in some of these doActions that perhaps should be here instead
        }
    }

    fun pushRight() {
        log("pushRight")
        val s = menus.peek().getSelected()
        when (s) {

            is MultiPrefMenuItem, is NumPrefMenuItem, is BinPrefMenuItem, is MultiChoiceMenuItem -> {
                s.doAction()
                Resources.BLING.play()
            }
            else -> {
                log("pushRight nothing")
            }
        }
    }

    fun pushLeft() {
        log("pushLeft")
        val s = menus.peek().getSelected()
        s.doAction2()
    }

    fun doInput() {

        val menu = menus.peek()

        if (menu.editing is StringPrefMenuItem) {
            doTextInput(menu.editing as StringPrefMenuItem)
            return
        }

        when {
            inputUp() -> {
                menu.up()
            }
            inputDown() -> {
                menu.down()
            }
            inputSelect() -> {
                select()
            }
            inputLeft() -> {
                pushLeft()
            }
            inputRight() -> {
                pushRight()
            }
            inputBack() -> {
                log("menucontroller back")

                doBack()
            }
        }
    }

    private fun doBack() {
        menus.pop()

        if (menus.isEmpty()) {
            menus.push(rootMenu)
            rootMenu.quitAction()
        }
    }

    val chars = "       0123456789            ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    private fun doTextInput(item: StringPrefMenuItem) {

        val editing = item.stringPref
        for (i in Input.Keys.NUM_0..Input.Keys.Z) {
            if (input.isKeyJustPressed(i)) {
                editing.appendChar(chars[i])
            }
        }
        if (input.isKeyJustPressed(Input.Keys.PERIOD)) {
            editing.appendChar('.')
        }
        if (input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            editing.deleteChar()
        }
        if (input.isKeyJustPressed(Input.Keys.ENTER)) {
            menus.peek().editing = null
        }
    }

    fun doMouseInput(x: Float, y: Float) {
        if (mouseMoved(x, y)) {
            mouseSelect(menus.peek(), x, y)
        }
    }

    val lineHeightPixels: Float = -font.data.down

    @Suppress("UNUSED_PARAMETER")
    private fun mouseSelect(menu: Menu, mx: Float, my: Float) {
    //    println("${font.data.lineHeight} ${font.data.ascent} ${font.data.descent} ${font.data.down} ${font.data.blankLineScale}")

        //  log("mouseselect $y")
        var counter = 0f
        for (i in menu.indices) {

            val d = if (menu.doubleSpaced) 2 else 2
            val top = y - d * lineHeightPixels - counter - lineHeightPixels / 2
            // val bottom = top - lineHeightPixels - lineHeightPixels / 2
            counter += (if (menu.doubleSpaced) 2 else 1) * lineHeightPixels

            // log(" item $i top $top bottom $bottom")
            if (my < top && !menu.get(i).isHidden) {
              menu.selectedItem = i
            }
        }
    }

    var oldY = 0f
    var oldX = 0f

    private fun mouseMoved(x: Float, y: Float): Boolean {
        val ty = oldY
        val tx = oldX
        oldY = y
        oldX = x
        if (tx == 0f && ty == 0f) {
            return false
        }
        if (tx == x && ty == y) {
            return false
        }
        return true
    }

    private fun inputBack() =
            input.isKeyJustPressed(ESCAPE) ||
                    input.isKeyJustPressed(BACK) ||
                    app.statefulControllers.any { it.isStartButtonJustPressed } ||
                    app.statefulControllers.any { it.isButtonBJustPressed }

    private fun inputSelect() =
    //  input.isKeyJustPressed(RIGHT) ||
            input.isKeyJustPressed(SPACE) ||
                    input.isKeyJustPressed(ENTER) ||
                    app.mouseJustClicked ||
                    app.statefulControllers.any { it.isButtonAJustPressed }

    private fun inputRight() =
            input.isKeyJustPressed(RIGHT) ||
                    app.statefulControllers.any { it.isRightButtonJustPressed }

    private fun inputLeft() =
            input.isKeyJustPressed(LEFT) ||
                    app.statefulControllers.any { it.isLeftButtonJustPressed }

    private fun inputDown() =
            input.isKeyJustPressed(DOWN) ||
                    app.statefulControllers.any { it.isDownButtonJustPressed }

    private fun inputUp() =
            input.isKeyJustPressed(UP) ||
                    app.statefulControllers.any { it.isUpButtonJustPressed }
}