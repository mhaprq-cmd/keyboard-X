package com.keyboardx.app.ime.styles

import com.keyboardx.app.R
import com.keyboardx.app.ime.KeyboardStyle
import com.keyboardx.app.ime.KeyboardStyleStore

/**
 * Default keyboard: no background, only the keys on the theme color.
 */
object DefaultKeyboardStyle {

    val style = KeyboardStyle(
        id = KeyboardStyleStore.DEFAULT_STYLE_ID,
        nameRes = R.string.keyboard_style_default_name,
        descriptionRes = R.string.keyboard_style_default_desc,
        backgroundRes = null,
        isNew = false
    )
}
