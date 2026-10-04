package com.keyboardx.app.ime

import android.view.KeyEvent

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
        ARABIC,
        ENGLISH
    }
}

object KeyboardLayoutProvider {

    fun getEnglishLayout(): KeyboardLayout {
        return KeyboardLayout(
            name = "English QWERTY",
            rows = listOf(
                Row(
                    listOf(
                        Key(KeyEvent.KEYCODE_1, "1"),
                        Key(KeyEvent.KEYCODE_2, "2"),
                        Key(KeyEvent.KEYCODE_3, "3"),
                        Key(KeyEvent.KEYCODE_4, "4"),
                        Key(KeyEvent.KEYCODE_5, "5"),
                        Key(KeyEvent.KEYCODE_6, "6"),
                        Key(KeyEvent.KEYCODE_7, "7"),
                        Key(KeyEvent.KEYCODE_8, "8"),
                        Key(KeyEvent.KEYCODE_9, "9"),
                        Key(KeyEvent.KEYCODE_0, "0")
                    )
                ),
                Row(
                    listOf(
                        Key(KeyEvent.KEYCODE_Q, "q"),
                        Key(KeyEvent.KEYCODE_W, "w"),
                        Key(KeyEvent.KEYCODE_E, "e"),
                        Key(KeyEvent.KEYCODE_R, "r"),
                        Key(KeyEvent.KEYCODE_T, "t"),
                        Key(KeyEvent.KEYCODE_Y, "y"),
                        Key(KeyEvent.KEYCODE_U, "u"),
                        Key(KeyEvent.KEYCODE_I, "i"),
                        Key(KeyEvent.KEYCODE_O, "o"),
                        Key(KeyEvent.KEYCODE_P, "p")
                    )
                ),
                Row(
                    listOf(
                        Key(KeyEvent.KEYCODE_A, "a"),
                        Key(KeyEvent.KEYCODE_S, "s"),
                        Key(KeyEvent.KEYCODE_D, "d"),
                        Key(KeyEvent.KEYCODE_F, "f"),
                        Key(KeyEvent.KEYCODE_G, "g"),
                        Key(KeyEvent.KEYCODE_H, "h"),
                        Key(KeyEvent.KEYCODE_J, "j"),
                        Key(KeyEvent.KEYCODE_K, "k"),
                        Key(KeyEvent.KEYCODE_L, "l")
                    )
                ),
                Row(
                    listOf(
                        Key(KeyEvent.KEYCODE_SHIFT_LEFT, "⇧"),
                        Key(KeyEvent.KEYCODE_Z, "z"),
                        Key(KeyEvent.KEYCODE_X, "x"),
                        Key(KeyEvent.KEYCODE_C, "c"),
                        Key(KeyEvent.KEYCODE_V, "v"),
                        Key(KeyEvent.KEYCODE_B, "b"),
                        Key(KeyEvent.KEYCODE_N, "n"),
                        Key(KeyEvent.KEYCODE_M, "m"),
                        Key(KeyEvent.KEYCODE_DEL, "⌫")
                    )
                ),
                Row(
                    listOf(
                        Key(KeyEvent.KEYCODE_COMMA, ","),
                        Key(KeyEvent.KEYCODE_SPACE, "Space", " "),
                        Key(KeyEvent.KEYCODE_PERIOD, "."),
                        Key(KeyEvent.KEYCODE_ENTER, "↵")
                    )
                )
            ),
            language = KeyboardLayout.Language.ENGLISH
        )
    }

    fun getArabicLayout(): KeyboardLayout {
        return KeyboardLayout(
            name = "Arabic",
            rows = emptyList(),
            language = KeyboardLayout.Language.ARABIC
        )
    }
}
