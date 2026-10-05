package com.keyboardx.app.ime

import android.view.KeyEvent

data class ArabicKeyDefinition(
    val character: String,
    val longPressVariants: List<String> = emptyList()
)

object ArabicKeyboard {

    val keys: List<ArabicKeyDefinition> = listOf(
        ArabicKeyDefinition(
            "ا",
            listOf("أ", "إ", "آ", "ٱ")
        ),
        ArabicKeyDefinition(
            "ب",
            listOf("پ")
        ),
        ArabicKeyDefinition(
            "ت",
            listOf("ٹ")
        ),
        ArabicKeyDefinition("ث"),
        ArabicKeyDefinition(
            "ج",
            listOf("چ")
        ),
        ArabicKeyDefinition(
            "ح",
            listOf("څ")
        ),
        ArabicKeyDefinition("خ"),
        ArabicKeyDefinition("د"),
        ArabicKeyDefinition("ذ"),
        ArabicKeyDefinition("ر"),
        ArabicKeyDefinition(
            "ز",
            listOf("ژ")
        ),
        ArabicKeyDefinition(
            "س",
            listOf("ڜ")
        ),
        ArabicKeyDefinition("ش"),
        ArabicKeyDefinition("ص"),
        ArabicKeyDefinition("ض"),
        ArabicKeyDefinition("ط"),
        ArabicKeyDefinition("ظ"),
        ArabicKeyDefinition("ع"),
        ArabicKeyDefinition("غ"),
        ArabicKeyDefinition(
            "ف",
            listOf("ڢ", "ڤ", "ڥ")
        ),
        ArabicKeyDefinition(
            "ق",
            listOf("ڨ", "ڧ", "ٯ")
        ),
        ArabicKeyDefinition(
            "ك",
            listOf("ک", "گ")
        ),
        ArabicKeyDefinition("ل"),
        ArabicKeyDefinition("م"),
        ArabicKeyDefinition(
            "ن",
            listOf("ں")
        ),
        ArabicKeyDefinition(
            "ه",
            listOf("ھ")
        ),
        ArabicKeyDefinition("و"),
        ArabicKeyDefinition(
            "ي",
            listOf("ى", "ی")
        ),
        ArabicKeyDefinition("ة"),
        ArabicKeyDefinition(
            "ء",
            listOf("ئ", "ؤ")
        )
    )

    private val variantsByCharacter: Map<String, List<String>> =
        keys.associate { it.character to it.longPressVariants }

    fun getLongPressVariants(character: String): List<String> {
        return variantsByCharacter[character].orEmpty()
    }

    fun hasLongPressVariants(character: String): Boolean {
        return getLongPressVariants(character).isNotEmpty()
    }

    fun getLayout(): KeyboardLayout {
        return KeyboardLayout(
            name = "Arabic",
            language = KeyboardLayout.Language.ARABIC,
            mode = KeyboardMode.ENGLISH,
            rows = listOf(
                Row(
                    listOf(
                        key("ض"),
                        key("ص"),
                        key("ث"),
                        key("ق"),
                        key("ف"),
                        key("غ"),
                        key("ع"),
                        key("ه"),
                        key("خ"),
                        key("ح")
                    )
                ),
                Row(
                    listOf(
                        key("ش"),
                        key("س"),
                        key("ي"),
                        key("ب"),
                        key("ل"),
                        key("ا"),
                        key("ت"),
                        key("ن"),
                        key("م"),
                        key("ك")
                    )
                ),
                Row(
                    listOf(
                        key("ظ"),
                        key("ط"),
                        key("ذ"),
                        key("د"),
                        key("ز"),
                        key("ر"),
                        key("و"),
                        key("ة"),
                        key("ء"),
                        key("ئ")
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
                        Key(
                            KeyEvent.KEYCODE_COMMA,
                            "،",
                            "،"
                        ),
                        Key(
                            KeyEvent.KEYCODE_SPACE,
                            "Space",
                            " ",
                            KeyAction.SPACE
                        ),
                        Key(
                            KeyEvent.KEYCODE_PERIOD,
                            "؛",
                            "؛"
                        ),
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

    private fun key(character: String): Key {
        return Key(
            KeyEvent.KEYCODE_UNKNOWN,
            character,
            character
        )
    }
}
