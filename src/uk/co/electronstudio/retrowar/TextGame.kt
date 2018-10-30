package uk.co.electronstudio.retrowar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import uk.co.electronstudio.retrowar.menu.ActionMenuItem
import uk.co.electronstudio.retrowar.menu.BackMenuItem
import uk.co.electronstudio.retrowar.menu.Menu
import uk.co.electronstudio.retrowar.menu.MenuController
import uk.co.electronstudio.retrowar.screens.GameSession
import uk.co.electronstudio.retrowar.utils.Vec

class TextGame(
    session: GameSession,
    text: String,
    width: Float = 416f,
    height: Float = 256f,
    font: BitmapFont =
        Resources.FONT
) :
    SimpleGame(
        session,
        width,
        height,
        font,
        false
    ) {


    val menu =
        Menu(
            "",
            bottomText = { text },
            doubleSpaced = true,
            quitAction = ::gameover,
            allItems = arrayListOf
                (
                BackMenuItem(
                    "OK"
                )
            )
        )


    val controller =
        MenuController(
            menu,
            width / 2,
            height,
            y = height - 20f
        )


    // FIXME MAKE THIS APPEAR IN A BOX IN tHE MIDDLE OF THE SCREEN LIKE THE ROUND NUMBER BOXES DO!!!

    override fun doDrawing(
        batch: Batch
    ) {
        controller.draw(
            batch
        )
    }

    override fun doLogic(
        deltaTime: Float
    ) {
        controller.doInput()
        val mouse =
            Vec(
                Gdx.input.x.toFloat(),
                Gdx.input.y.toFloat()
            )
        controller.doMouseInput(
            mouse.x,
            mouse.y
        )
    }

    override fun show() {
        App.app.clearEvents()
    }

    override fun hide() {

    }

    override fun dispose() {

    }
}