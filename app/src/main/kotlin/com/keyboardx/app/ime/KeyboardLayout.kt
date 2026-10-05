package com.keyboardx.app.ime

import android.view.KeyEvent

enum class KeyboardMode {
    ENGLISH,
    NUMBERS,
    SYMBOLS
}

enum class KeyAction {
    NONE,
    SHIFT,
    DELETE,
    SPACE,
    ENTER,
    SWITCH_TO_ENGLISH,
    SWITCH_TO_NUMBERS,
    SWITCH_TO_SYMBOLS,
    SWITCH_LANGUAGE
}

data class Key(
    val code: Int,
    val label: String,
    val outputText: String? = null,
    val action: KeyAction = KeyAction.NONE
)

data class Row(
    val keys: List<Key>
)

data class KeyboardLayout(
    val name: String,
    val rows: List<Row>,
    val language: Language,
    val mode: KeyboardMode
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
            language = KeyboardLayout.Language.ENGLISH,
            mode = KeyboardMode.ENGLISH,
            rows = listOf(
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
                        Key(
                            KeyEvent.KEYCODE_SHIFT_LEFT,
                            "⇧",
                            action = KeyAction.SHIFT
                        ),
                        Key(KeyEvent.KEYCODE_Z, "z"),
                        Key(KeyEvent.KEYCODE_X, "x"),
                        Key(KeyEvent.KEYCODE_C, "c"),
                        Key(KeyEvent.KEYCODE_V, "v"),
                        Key(KeyEvent.KEYCODE_B, "b"),
                        Key(KeyEvent.KEYCODE_N, "n"),
                        Key(KeyEvent.KEYCODE_M, "m"),
                        Key(
                            KeyEvent.KEYCODE_DEL,
                            "⌫",
                            action = KeyAction.DELETE
                        )
                    )
                ),
                Row(
                    listOf(
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "123",
                            action = KeyAction.SWITCH_TO_NUMBERS
                        ),
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "🌐",
                            action = KeyAction.SWITCH_LANGUAGE
                        ),
                        Key(KeyEvent.KEYCODE_COMMA, ","),
                        Key(
                            KeyEvent.KEYCODE_SPACE,
                            "Space",
                            " ",
                            KeyAction.SPACE
                        ),
                        Key(KeyEvent.KEYCODE_PERIOD, "."),
                        Key(
                            KeyEvent.KEYCODE_ENTER,
                            "↵",
                            action = KeyAction.ENTER
                        ),
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "#+=",
                            action = KeyAction.SWITCH_TO_SYMBOLS
                        )
                    )
                )
            )
        )
    }

    fun getNumbersLayout(): KeyboardLayout {
        return KeyboardLayout(
            name = "Numbers",
            language = KeyboardLayout.Language.ENGLISH,
            mode = KeyboardMode.NUMBERS,
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
                        Key(KeyEvent.KEYCODE_PERCENT, "%"),
                        Key(KeyEvent.KEYCODE_PLUS, "+"),
                        Key(KeyEvent.KEYCODE_MINUS, "-"),
                        Key(KeyEvent.KEYCODE_STAR, "*"),
                        Key(KeyEvent.KEYCODE_SLASH, "/"),
                        Key(KeyEvent.KEYCODE_COMMA, ","),
                        Key(KeyEvent.KEYCODE_PERIOD, "."),
                        Key(KeyEvent.KEYCODE_EQUALS, "=")
                    )
                ),
                Row(
                    listOf(
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "#+=",
                            action = KeyAction.SWITCH_TO_SYMBOLS
                        ),
                        Key(KeyEvent.KEYCODE_LEFT_PAREN, "("),
                        Key(KeyEvent.KEYCODE_RIGHT_PAREN, ")"),
                        Key(KeyEvent.KEYCODE_LEFT_BRACKET, "["),
                        Key(KeyEvent.KEYCODE_RIGHT_BRACKET, "]"),
                        Key(
                            KeyEvent.KEYCODE_DEL,
                            "⌫",
                            action = KeyAction.DELETE
                        )
                    )
                ),
                Row(
                    listOf(
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "ABC",
                            action = KeyAction.SWITCH_TO_ENGLISH
                        ),
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "🌐",
                            action = KeyAction.SWITCH_LANGUAGE
                        ),
                        Key(
                            KeyEvent.KEYCODE_SPACE,
                            "Space",
                            " ",
                            KeyAction.SPACE
                        ),
                        Key(
                            KeyEvent.KEYCODE_ENTER,
                            "↵",
                            action = KeyAction.ENTER
                        )
                    )
                )
            )
        )
    }

    fun getSymbolsLayout(): KeyboardLayout {
        return KeyboardLayout(
            name = "Symbols",
            language = KeyboardLayout.Language.ENGLISH,
            mode = KeyboardMode.SYMBOLS,
            rows = listOf(
                Row(
                    listOf(
                        Key(KeyEvent.KEYCODE_UNKNOWN, "∆"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "§"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "×"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "÷"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "π"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "√"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "•"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "|")
                    )
                ),
                Row(
                    listOf(
                        Key(KeyEvent.KEYCODE_UNKNOWN, "`"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "~"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "£"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "¢"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "€"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "¥"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "^"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "°")
                    )
                ),
                Row(
                    listOf(
                        Key(KeyEvent.KEYCODE_UNKNOWN, "="),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "{"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "}"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "["),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "]"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "\\"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "٪"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "✓")
                    )
                ),
                Row(
                    listOf(
                        Key(KeyEvent.KEYCODE_UNKNOWN, "™"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "®"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "©"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "»"),
                        Key(KeyEvent.KEYCODE_UNKNOWN, "«"),
                        Key(
                            KeyEvent.KEYCODE_DEL,
                            "⌫",
                            action = KeyAction.DELETE
                        )
                    )
                ),
                Row(
                    listOf(
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "123",
                            action = KeyAction.SWITCH_TO_NUMBERS
                        ),
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "ABC",
                            action = KeyAction.SWITCH_TO_ENGLISH
                        ),
                        Key(
                            KeyEvent.KEYCODE_SPACE,
                            "Space",
                            " ",
                            KeyAction.SPACE
                        ),
                        Key(
                            KeyEvent.KEYCODE_ENTER,
                            "↵",
                            action = KeyAction.ENTER
                        )
                    )
                )
            )
        )
    }

    fun getArabicLayout(): KeyboardLayout {
        return KeyboardLayout(
            name = "Arabic",
            rows = emptyList(),
            language = KeyboardLayout.Language.ARABIC,
            mode = KeyboardMode.ENGLISH
        )
    }

    fun getLayout(mode: KeyboardMode): KeyboardLayout {
        return when (mode) {
            KeyboardMode.ENGLISH -> getEnglishLayout()
            KeyboardMode.NUMBERS -> getNumbersLayout()
            KeyboardMode.SYMBOLS -> getSymbolsLayout()
        }
    }
}
