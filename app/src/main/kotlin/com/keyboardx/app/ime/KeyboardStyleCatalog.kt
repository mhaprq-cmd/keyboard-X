package com.keyboardx.app.ime

import android.content.Context
import com.keyboardx.app.ime.styles.DefaultKeyboardStyle
import com.keyboardx.app.ime.styles.ImageKeyboardStyle

/**
 * The list of keyboards shown on the home screen.
 *
 * To add a keyboard:
 * 1. Create a new file inside the "styles" folder (copy ImageKeyboardStyle).
 * 2. Add its name and description strings in values/ and values-ar/.
 * 3. Add ONE line to the list below.
 *
 * The first item must stay the default keyboard. It is the fallback
 * when a saved style id no longer exists.
 */
object KeyboardStyleCatalog {

    val all: List<KeyboardStyle> = listOf(
        DefaultKeyboardStyle.style,
        ImageKeyboardStyle.style
    )

    fun findById(id: String): KeyboardStyle {
        return all.firstOrNull { it.id == id }
            ?: DefaultKeyboardStyle.style
    }

    fun getSelected(context: Context): KeyboardStyle {
        return findById(KeyboardStyleStore.getSelectedId(context))
    }
}
