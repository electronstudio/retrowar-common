package uk.co.electronstudio.retrowar.menu

import com.badlogic.gdx.graphics.g2d.GlyphLayout

/**
 * Wraps a MenuController and draws the contained menu.
 * Depreciated!
 */
class MenuRenderer(val menu: Menu) {

    internal var glyphLayout = GlyphLayout()

    var count = 0

    val sequence = arrayOf("RED", "PURPLE", "BLUE", "CYAN", "GREEN", "YELLOW")

    var flash = ""

//    fun draw(batch: SpriteBatch) {
//        batch.begin()
//        flash = sequence[count++ % sequence.size]
//        glyphLayout.setText(Resources.FONT, menu.getText("RED"), Color.WHITE, Renderer.WIDTH, Align.center, true)
//        Resources.FONT.draw(batch, glyphLayout, 0f, Renderer.HEIGHT)
//        batch.end()
//    }
}
