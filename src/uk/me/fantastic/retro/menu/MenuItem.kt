package uk.me.fantastic.retro.menu

import uk.me.fantastic.retro.Prefs

/**
 * Menus are lists of MenuItems.  There are several distinct types....
 */

abstract class MenuItem(open val text: String) {
    abstract fun displayText(): String
    abstract fun doAction()
    open fun doAction2() {
        // return doAction()
    }

    var isHidden: Boolean = false
}

class GraphicalMenuItem

class BackMenuItem(text: String = "<<<<") : MenuItem(text) {
    override fun displayText() = ""
    override fun doAction() {
    }
}

class MultiPrefMenuItem(text: String, val mPref: Prefs.MultiChoicePref, val action: () -> Unit = {}) : MenuItem(text) {
    override fun doAction() {
        mPref.next()
        action.invoke()
    }

    override fun doAction2() {
        mPref.prev()
        action.invoke()
    }

    override fun displayText(): String {
        return mPref.displayText()
    }
}

class ColorPrefMenuItem(text: String, val mPref: Prefs.MultiChoicePref, val action: () -> Unit = {}) : MenuItem(text) {
    override fun doAction() {
        mPref.next()
        action.invoke()
    }

    override fun doAction2() {
        mPref.prev()
        action.invoke()
    }

    override fun displayText(): String {
        return ""
    }

    override val text: String = text
        get() = "* [#${mPref.displayText()}]$field[]"
}

// fixme this really shouldnt be mutable.  there should just be some mutable wrapper around it to swap them when needed.
class MultiChoiceMenuItem(
        text: String,
        val onUpdate: (String, Int) -> Unit = { _, _ -> run {} },
        var choices: List<String>,
        val
    intValues: List<Int>
) : MenuItem(text) {
    var selected = 0

    override fun doAction() {
        selected++

        if (selected > choices.lastIndex) {
            selected = 0
        }
        onUpdate(choices[selected], intValues[selected])
    }

    override fun doAction2() {
        selected--

        if (selected < 0) {
            selected = choices.lastIndex
        }
        onUpdate(choices[selected], intValues[selected])
    }

    override fun displayText(): String {
        return if (isHidden) "hidden!" else choices[selected]
    }

    fun getSelectedInt(): Int {
        val i = intValues.get(selected)
        return i
    }
}

class NumPrefMenuItem(text: String, val numPref: Prefs.NumPref) : MenuItem(text) {
    override fun displayText() = numPref.displayText()
    override fun doAction() {
        numPref.increase()
        numPref.apply()
    }

    override fun doAction2() {
        numPref.decreass()
    }
}

class StringPrefMenuItem(text: String, val stringPref: Prefs.StringPref) : MenuItem(text) {
    override fun displayText() = stringPref.displayText()
    override fun doAction() {
    }
}

class BinPrefMenuItem(text: String, val binPref: Prefs.BinPref) : MenuItem(text) {
    override fun displayText() = binPref.displayText()
    override fun doAction() {
        binPref.toggle()
    }
}

class ActionMenuItem(text: String, val action: () -> Unit) : MenuItem(text) {
    override fun displayText() = ""
    override fun doAction() {
        action.invoke()
    }
}

class SubMenuItem(text: String, val subMenu: Menu) : MenuItem(text) {
    override fun displayText() = ""
    override fun doAction() {
    }
}

class EmptyMenuItem : MenuItem("") {
    init {
        isHidden = true
    }

    override fun displayText() = ""
    override fun doAction() {
    }
}