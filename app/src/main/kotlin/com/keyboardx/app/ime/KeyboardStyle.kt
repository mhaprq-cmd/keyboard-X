package com.keyboardx.app.ime

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Describes one keyboard that appears in the home screen catalog.
 *
 * Each keyboard lives in its own file inside the "styles" folder and is
 * registered with one line in KeyboardStyleCatalog.
 *
 * Light and dark colors are NOT part of a style. They keep following the
 * Android system mode through KeyboardThemeManager.
 *
 * @param id unique and permanent. It is saved in SharedPreferences.
 * @param nameRes string resource for the displayed name.
 * @param descriptionRes string resource for the short description.
 * @param backgroundRes drawable drawn behind the keys, or null for none.
 * @param isNew shows the small "New" badge in the catalog.
 */
data class KeyboardStyle(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val backgroundRes: Int? = null,
    val isNew: Boolean = false
)
