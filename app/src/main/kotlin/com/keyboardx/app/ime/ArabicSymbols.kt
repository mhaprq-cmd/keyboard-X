package com.keyboardx.app.ime

import android.view.KeyEvent

object ArabicSymbols {

    val arabicNumerals: List<KeyboardSymbol> = listOf(
        KeyboardSymbol("٠"),
        KeyboardSymbol("١"),
        KeyboardSymbol("٢"),
        KeyboardSymbol("٣"),
        KeyboardSymbol("٤"),
        KeyboardSymbol("٥"),
        KeyboardSymbol("٦"),
        KeyboardSymbol("٧"),
        KeyboardSymbol("٨"),
        KeyboardSymbol("٩")
    )

    val arabicPunctuation: List<KeyboardSymbol> = listOf(
        KeyboardSymbol("،"),
        KeyboardSymbol("؛"),
        KeyboardSymbol("؟"),
        KeyboardSymbol("٫"),
        KeyboardSymbol("٬"),
        KeyboardSymbol("٪"),
        KeyboardSymbol("ـ"),
        KeyboardSymbol("«"),
        KeyboardSymbol("»"),
        KeyboardSymbol("‹"),
        KeyboardSymbol("›"),
        KeyboardSymbol("“"),
        KeyboardSymbol("”"),
        KeyboardSymbol("‘"),
        KeyboardSymbol("’"),
        KeyboardSymbol("…"),
        KeyboardSymbol("—"),
        KeyboardSymbol("–"),
        KeyboardSymbol("•")
    )

    val arabicDiacritics: List<KeyboardSymbol> = listOf(
        KeyboardSymbol("َ"),
        KeyboardSymbol("ً"),
        KeyboardSymbol("ُ"),
        KeyboardSymbol("ٌ"),
        KeyboardSymbol("ِ"),
        KeyboardSymbol("ٍ"),
        KeyboardSymbol("ْ"),
        KeyboardSymbol("ّ"),
        KeyboardSymbol("ٰ"),
        KeyboardSymbol("ٓ"),
        KeyboardSymbol("ٔ"),
        KeyboardSymbol("ٕ")
    )

    val mathematicalOperators: List<KeyboardSymbol> = listOf(
        KeyboardSymbol("+"),
        KeyboardSymbol("-"),
        KeyboardSymbol("*"),
        KeyboardSymbol("/"),
        KeyboardSymbol("%"),
        KeyboardSymbol("="),
        KeyboardSymbol("×"),
        KeyboardSymbol("÷"),
        KeyboardSymbol("±"),
        KeyboardSymbol("≠"),
        KeyboardSymbol("≈"),
        KeyboardSymbol("≤"),
        KeyboardSymbol("≥"),
        KeyboardSymbol("∞"),
        KeyboardSymbol("√"),
        KeyboardSymbol("∑"),
        KeyboardSymbol("π"),
        KeyboardSymbol("∆")
    )

    val bracketsAndStructure: List<KeyboardSymbol> = listOf(
        KeyboardSymbol("("),
        KeyboardSymbol(")"),
        KeyboardSymbol("["),
        KeyboardSymbol("]"),
        KeyboardSymbol("{"),
        KeyboardSymbol("}"),
        KeyboardSymbol("<"),
        KeyboardSymbol(">"),
        KeyboardSymbol("|"),
        KeyboardSymbol("\\"),
        KeyboardSymbol("_"),
        KeyboardSymbol("~"),
        KeyboardSymbol("^")
    )

    val commonSymbols: List<KeyboardSymbol> = listOf(
        KeyboardSymbol("!"),
        KeyboardSymbol("@"),
        KeyboardSymbol("#"),
        KeyboardSymbol("$"),
        KeyboardSymbol("&"),
        KeyboardSymbol("©"),
        KeyboardSymbol("®"),
        KeyboardSymbol("™"),
        KeyboardSymbol("°"),
        KeyboardSymbol("§"),
        KeyboardSymbol("•"),
        KeyboardSymbol("…")
    )

    val all: List<KeyboardSymbol> =
        (
            arabicNumerals +
                arabicPunctuation +
                arabicDiacritics +
                mathematicalOperators +
                bracketsAndStructure +
                commonSymbols
            ).distinctBy { it.outputText }

    fun getNumbersLayout(): KeyboardLayout {
        return KeyboardLayout(
            name = "Arabic Numbers",
            language = KeyboardLayout.Language.ARABIC,
            mode = KeyboardMode.NUMBERS,
            rows = listOf(
                Row(
                    listOf(
                        key("١"),
                        key("٢"),
                        key("٣"),
                        key("٤"),
                        key("٥"),
                        key("٦"),
                        key("٧"),
                        key("٨"),
                        key("٩"),
                        key("٠")
                    )
                ),
                Row(
                    listOf(
                        key("%"),
                        key("+"),
                        key("-"),
                        key("*"),
                        key("/"),
                        key("="),
                        key("×"),
                        key("÷")
                    )
                ),
                Row(
                    listOf(
                        key("،"),
                        key("؛"),
                        key("؟"),
                        key("٫"),
                        key("٬"),
                        key("٪"),
                        key("("),
                        key(")")
                    )
                ),
                Row(
                    listOf(
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "حروف",
                            action = KeyAction.SWITCH_TO_ENGLISH
                        ),
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "🌐",
                            action = KeyAction.SWITCH_LANGUAGE
                        ),
                        Key(
                            KeyEvent.KEYCODE_SPACE,
                            "",
                            " ",
                            KeyAction.SPACE,
                            widthWeight = 2.5f
                        ),
                        Key(
                            KeyEvent.KEYCODE_DEL,
                            "⌫",
                            action = KeyAction.DELETE
                        ),
                        Key(
                            KeyEvent.KEYCODE_ENTER,
                            "↵",
                            action = KeyAction.ENTER
                        ),
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "🔍",
                            action = KeyAction.SEARCH
                        )
                    )
                )
            )
        )
    }

    fun getSymbolsLayout(): KeyboardLayout {
        return KeyboardLayout(
            name = "Arabic Symbols",
            language = KeyboardLayout.Language.ARABIC,
            mode = KeyboardMode.SYMBOLS,
            rows = listOf(
                Row(
                    listOf(
                        key("≠"),
                        key("≈"),
                        key("≤"),
                        key("≥"),
                        key("±"),
                        key("∞"),
                        key("√"),
                        key("π")
                    )
                ),
                Row(
                    listOf(
                        key("("),
                        key(")"),
                        key("["),
                        key("]"),
                        key("{"),
                        key("}"),
                        key("<"),
                        key(">")
                    )
                ),
                Row(
                    listOf(
                        key("«"),
                        key("»"),
                        key("‹"),
                        key("›"),
                        key("“"),
                        key("”"),
                        key("‘"),
                        key("’")
                    )
                ),
                Row(
                    listOf(
                        key("ـ"),
                        key("…"),
                        key("—"),
                        key("–"),
                        key("•"),
                        key("©"),
                        key("®"),
                        key("™")
                    )
                ),
                Row(
                    listOf(
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "١٢٣",
                            action = KeyAction.SWITCH_TO_NUMBERS
                        ),
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "حروف",
                            action = KeyAction.SWITCH_TO_ENGLISH
                        ),
                        Key(
                            KeyEvent.KEYCODE_SPACE,
                            "",
                            " ",
                            KeyAction.SPACE,
                            widthWeight = 2.5f
                        ),
                        Key(
                            KeyEvent.KEYCODE_DEL,
                            "⌫",
                            action = KeyAction.DELETE
                        ),
                        Key(
                            KeyEvent.KEYCODE_ENTER,
                            "↵",
                            action = KeyAction.ENTER
                        ),
                        Key(
                            KeyEvent.KEYCODE_UNKNOWN,
                            "🔍",
                            action = KeyAction.SEARCH
                        )
                    )
                )
            )
        )
    }

    private fun key(symbol: String): Key {
        return Key(
            KeyEvent.KEYCODE_UNKNOWN,
            symbol,
            symbol
        )
    }
}
