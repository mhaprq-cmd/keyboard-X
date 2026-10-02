package com.keyboardx.app.ime

/**
 * Data class representing keyboard layout configuration.
 * Supports Arabic and English layouts.
 */
data class Key(
    val code: Int,
    val label: String,
    val outputText: String? = null
)

data class Row(
    val keys: List<Key>
)

data class KeyboardLayout(
    val name: String,
    val rows: List<Row>,
    val language: Language
) {
    enum class Language {
        ARABIC, ENGLISH
    }
}

/**
 * Keyboard layout provider.
 * Manages different keyboard layouts for supported languages.
 */
object KeyboardLayoutProvider {

    fun getEnglishLayout(): KeyboardLayout {
        // Placeholder for English QWERTY layout
        return KeyboardLayout(
            name = "English QWERTY",
            rows = emptyList(),
            language = KeyboardLayout.Language.ENGLISH
        )
    }

    fun getArabicLayout(): KeyboardLayout {
        // Placeholder for Arabic layout
        return KeyboardLayout(
            name = "Arabic",
            rows = emptyList(),
            language = KeyboardLayout.Language.ARABIC
        )
    }
}