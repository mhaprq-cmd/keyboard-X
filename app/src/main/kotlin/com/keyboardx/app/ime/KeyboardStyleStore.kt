package com.keyboardx.app.ime

import android.content.Context

/**
 * Saves and reads the selected keyboard style.
 *
 * Uses the same SharedPreferences file as the app and the keyboard
 * ("keyboard_x_preferences"), so a choice made in the app is read
 * by KeyboardInputView the next time the keyboard opens.
 */
object KeyboardStyleStore {

    private const val PREFERENCES_NAME = "keyboard_x_preferences"
    private const val KEY_SELECTED_STYLE = "selected_keyboard_style"

    const val DEFAULT_STYLE_ID = "default"

    fun getSelectedId(context: Context): String {
        return context
            .getSharedPreferences(
                PREFERENCES_NAME,
                Context.MODE_PRIVATE
            )
            .getString(KEY_SELECTED_STYLE, DEFAULT_STYLE_ID)
            ?: DEFAULT_STYLE_ID
    }

    fun setSelectedId(context: Context, id: String) {
        context
            .getSharedPreferences(
                PREFERENCES_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(KEY_SELECTED_STYLE, id)
            .apply()
    }
}
