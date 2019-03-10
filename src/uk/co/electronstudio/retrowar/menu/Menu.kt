package uk.co.electronstudio.retrowar.menu

import uk.co.electronstudio.retrowar.Resources
import uk.co.electronstudio.retrowar.log
import java.util.ArrayList

/**
 * Created by richard on 13/07/2016.
 * Essentially an ArrayList of MenuItems that tracks which of them is selected
 */

typealias Page = ArrayList<MenuItem>

open class Menu(
    val title: String,
    val bottomText: () -> String = { "" },
    val quitAction: () -> Unit = {},
    val doubleSpaced: Boolean = true, // Resources.FONT.data.down > -10,
    val allItems: ArrayList<MenuItem> = ArrayList()) {
    val pages: ArrayList<Page> = ArrayList()
    var currentPage: ArrayList<MenuItem>

    init {
        pages.add(allItems)
        currentPage = pages[0]
    }

    internal var selectedItem = 0

    var editing: MenuItem? = null
    internal val indices: IntRange
        get() = currentPage.indices
    val selectedItemIndex: Int
        get() = allItems.indexOf(currentPage[selectedItem])

    fun paginate(maxLength: Int) {
        pages.clear()
        pages.add(Page())
        var c = 0
        for (item in allItems) {
            if (c > maxLength) {
                val page = pages.last()
                val nextPage = Page()
                pages.add((nextPage))
                page.add(ActionMenuItem("[...]", action = {
                    currentPage = nextPage
                    selectedItem = 0
                }))
                nextPage.add(ActionMenuItem("[...]", action = {
                    currentPage = page
                    selectedItem = 0
                }))
                c = 0
            }
            pages.last().add(item)
            c++
        }
        currentPage = pages.first()
    }

    fun addAll(elements: Collection<MenuItem>): Boolean {
        return allItems.addAll(elements)
    }

    operator fun get(i: Int): MenuItem {
        return currentPage.get(i)
    }

    fun add(item: MenuItem) {
        allItems.add(item)
    }

    fun addAndSelect(item: MenuItem) {
        allItems.add(item)
        selectedItem = allItems.lastIndex
    }

    fun getSelected(): MenuItem {
        return currentPage[selectedItem]
    }

    fun getText(highlight: String?): String {
        var text = "$title\n\n"

        currentPage.forEachIndexed { i, menuItem ->
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

        while (selectedItem > 0 && currentPage.get(selectedItem).isHidden) {
            selectedItem--
        }

        if (selectedItem < 0) selectedItem = currentPage.size - 1

        Resources.BLING.play()
    }

    fun down() {
        selectedItem++

        while (selectedItem < currentPage.size - 1 && currentPage.get(selectedItem).isHidden) {
            selectedItem++
        }

        if (selectedItem > currentPage.size - 1) selectedItem = 0

        Resources.BLING.play()
        log("selecteditem $selectedItem")
    }
}

class ScrollingMenu(title: String, bottomText: () -> String, quitAction: () -> Unit, doubleSpaced: Boolean) :
    Menu(title, bottomText, quitAction, doubleSpaced)