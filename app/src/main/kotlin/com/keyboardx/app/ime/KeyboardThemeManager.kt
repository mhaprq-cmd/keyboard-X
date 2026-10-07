package com.keyboardx.app.ime

import android.content.Context
import android.content.res.Configuration

/**
 * Phase 6 theme manager for Keyboard X.
 *
 * Responsible only for selecting the active visual theme.
 *
 * IMPORTANT:
 * - Does not change key width.
 * - Does not change key height.
 * - Does not change keyboard height.
 * - Does not change keyboard layout.
 * - Does not change keyboard behavior.
 *
 * The active theme follows the Android system light/dark mode.
 */
object KeyboardThemeManager {

    fun getCurrentTheme(context: Context): KeyboardTheme {
        return if (isDarkMode(context)) {
            KeyboardThemes.get(
                KeyboardThemeMode.DARK
            )
        } else {
            KeyboardThemes.get(
                KeyboardThemeMode.LIGHT
            )
        }
    }

    fun getCurrentMode(context: Context): KeyboardThemeMode {
        return if (isDarkMode(context)) {
            KeyboardThemeMode.DARK
        } else {
            KeyboardThemeMode.LIGHT
        }
    }

    private fun isDarkMode(context: Context): Boolean {
        val nightMode =
            context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        return nightMode == Configuration.UI_MODE_NIGHT_YES
    }
}
