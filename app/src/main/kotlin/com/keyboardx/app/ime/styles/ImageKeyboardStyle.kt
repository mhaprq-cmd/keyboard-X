package com.keyboardx.app.ime.styles

import com.keyboardx.app.R
import com.keyboardx.app.ime.KeyboardStyle

/**
 * Keyboard with the built-in background image drawn behind the keys.
 *
 * To add another keyboard, copy this file, change the id, the strings
 * and the drawable, then add one line to KeyboardStyleCatalog.
 */
object ImageKeyboardStyle {

    val style = KeyboardStyle(
        id = "image_background",
        nameRes = R.string.keyboard_style_image_name,
        descriptionRes = R.string.keyboard_style_image_desc,
        backgroundRes = R.drawable.keyboard_background_static,
        isNew = true
    )
}
