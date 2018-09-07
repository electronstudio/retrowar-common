package uk.me.fantastic.retro.menu

import uk.me.fantastic.retro.Resources
import uk.me.fantastic.retro.log
import java.util.ArrayList

/**
 * Created by richard on 13/07/2016.
 * Essentially an ArrayList of MenuItems that tracks which of them is selected
 */

class Menu(
    val title: String,
    val bottomText: () -> String = { "" },
    val quitAction: () -> Unit = {},

    val doubleSpaced: Boolean = Resources.FONT.data.down > -10
) : ArrayList<MenuItem>() {

    var selectedItem = 0

    var editing: MenuItem? = null

    fun getSelected(): MenuItem {
        return this[selectedItem]
    }

    fun getText(highlight: String?): String {
        var text = "$title\n\n"

        this.forEachIndexed { i, menuItem ->
            if (doubleSpaced) text += "\n"
            if (i == selectedItem && highlight != null) text = "$text[$highlight]"
            if (!menuItem.isHidden) {
                text += menuItem.text
                text += menuItem.displayText()
            }

            if (menuItem == editing) text += "*"
            text += "\n"
            // if (doubleSpaced) text += "\n"
            if (i == selectedItem && highlight != null) text += "[]"
        }

        return "$text${bottomText.invoke()}"
    }

    fun up() {
        selectedItem--

        while (selectedItem > 0 && get(selectedItem).isHidden) {
            selectedItem--
        }

        if (selectedItem < 0)
            selectedItem = size - 1

        Resources.BLING.play()
    }

    fun down() {
        selectedItem++

        while (selectedItem < size - 1 && get(selectedItem).isHidden) {
            selectedItem++
        }

        if (selectedItem > size - 1)
            selectedItem = 0

        Resources.BLING.play()
        log("selecteditem $selectedItem")
    }
}
