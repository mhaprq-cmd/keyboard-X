package com.keyboardx.app.ime

import android.view.KeyEvent

data class ArabicKeyDefinition(
    val character: String,
    val longPressVariants: List<String> = emptyList()
)

object ArabicKeyboard {

    val keys: List<ArabicKeyDefinition> = listOf(
        ArabicKeyDefinition("ج", listOf("چ")),
        ArabicKeyDefinition("ح"),
        ArabicKeyDefinition("خ"),
        ArabicKeyDefinition("ه"),
        ArabicKeyDefinition("ع"),
        ArabicKeyDefinition("غ"),
        ArabicKeyDefinition("ف", listOf("ڢ", "ڤ", "ڥ")),
        ArabicKeyDefinition("ق", listOf("ڧ", "ڨ", "ٯ")),
        ArabicKeyDefinition("ث"),
        ArabicKeyDefinition("ص"),
        ArabicKeyDefinition("ض"),
        ArabicKeyDefinition("ط"),
        ArabicKeyDefinition("ك", listOf("ک", "گ")),
        ArabicKeyDefinition("م"),
        ArabicKeyDefinition("ن"),
        ArabicKeyDefinition("ت"),
        ArabicKeyDefinition("ا", listOf("أ", "إ", "آ", "ٱ", "ء")),
        ArabicKeyDefinition("ل"),
        ArabicKeyDefinition("ب", listOf("پ")),
        ArabicKeyDefinition("ي", listOf("ئ", "ى", "ی")),
        ArabicKeyDefinition("س", listOf("ڜ")),
        ArabicKeyDefinition("ش"),
        ArabicKeyDefinition("د"),
        ArabicKeyDefinition("ظ"),
        ArabicKeyDefinition("ز", listOf("ژ")),
        ArabicKeyDefinition("و", listOf("ؤ")),
        ArabicKeyDefinition("ة"),
        ArabicKeyDefinition("ى"),
        ArabicKeyDefinition("ر"),
        ArabicKeyDefinition("ء")
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
                        key("ح"),
                        key("ج")
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
                        key("ى"),
                        key("ء")
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
                            "",
                            " ",
                            KeyAction.SPACE
                        ),
                        Key(
                            KeyEvent.KEYCODE_PERIOD,
                            "؛",
                            "؛"
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
