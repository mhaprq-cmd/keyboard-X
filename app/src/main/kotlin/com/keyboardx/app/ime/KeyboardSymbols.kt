package com.keyboardx.app.ime

data class KeyboardSymbol(
    val label: String,
    val outputText: String = label
)

object KeyboardSymbols {

    val englishPunctuation: List<KeyboardSymbol> = listOf(
        KeyboardSymbol("!"),
        KeyboardSymbol("\""),
        KeyboardSymbol("#"),
        KeyboardSymbol("$"),
        KeyboardSymbol("%"),
        KeyboardSymbol("&"),
        KeyboardSymbol("'"),
        KeyboardSymbol("("),
        KeyboardSymbol(")"),
        KeyboardSymbol("*"),
        KeyboardSymbol("+"),
        KeyboardSymbol(","),
        KeyboardSymbol("-"),
        KeyboardSymbol("."),
        KeyboardSymbol("/"),
        KeyboardSymbol(":"),
        KeyboardSymbol(";"),
        KeyboardSymbol("<"),
        KeyboardSymbol("="),
        KeyboardSymbol(">"),
        KeyboardSymbol("?"),
        KeyboardSymbol("@"),
        KeyboardSymbol("["),
        KeyboardSymbol("\\"),
        KeyboardSymbol("]"),
        KeyboardSymbol("^"),
        KeyboardSymbol("_"),
        KeyboardSymbol("`"),
        KeyboardSymbol("{"),
        KeyboardSymbol("|"),
        KeyboardSymbol("}"),
        KeyboardSymbol("~")
    )

    val englishExtended: List<KeyboardSymbol> = listOf(
        KeyboardSymbol("£"),
        KeyboardSymbol("€"),
        KeyboardSymbol("¥"),
        KeyboardSymbol("₽"),
        KeyboardSymbol("₹"),
        KeyboardSymbol("₩"),
        KeyboardSymbol("¢"),
        KeyboardSymbol("§"),
        KeyboardSymbol("©"),
        KeyboardSymbol("®"),
        KeyboardSymbol("™"),
        KeyboardSymbol("°"),
        KeyboardSymbol("±"),
        KeyboardSymbol("×"),
        KeyboardSymbol("÷"),
        KeyboardSymbol("≠"),
        KeyboardSymbol("≈"),
        KeyboardSymbol("≤"),
        KeyboardSymbol("≥"),
        KeyboardSymbol("∞"),
        KeyboardSymbol("√"),
        KeyboardSymbol("∑"),
        KeyboardSymbol("∏"),
        KeyboardSymbol("∂"),
        KeyboardSymbol("∆"),
        KeyboardSymbol("π"),
        KeyboardSymbol("µ"),
        KeyboardSymbol("‰"),
        KeyboardSymbol("¼"),
        KeyboardSymbol("½"),
        KeyboardSymbol("¾"),
        KeyboardSymbol("¹"),
        KeyboardSymbol("²"),
        KeyboardSymbol("³"),
        KeyboardSymbol("⁺"),
        KeyboardSymbol("⁻"),
        KeyboardSymbol("₀"),
        KeyboardSymbol("₁"),
        KeyboardSymbol("₂"),
        KeyboardSymbol("₃")
    )

    val quotationAndTypography: List<KeyboardSymbol> = listOf(
        KeyboardSymbol("“"),
        KeyboardSymbol("”"),
        KeyboardSymbol("‘"),
        KeyboardSymbol("’"),
        KeyboardSymbol("«"),
        KeyboardSymbol("»"),
        KeyboardSymbol("‹"),
        KeyboardSymbol("›"),
        KeyboardSymbol("…"),
        KeyboardSymbol("—"),
        KeyboardSymbol("–"),
        KeyboardSymbol("‐"),
        KeyboardSymbol("•"),
        KeyboardSymbol("·"),
        KeyboardSymbol("‣"),
        KeyboardSymbol("†"),
        KeyboardSymbol("‡"),
        KeyboardSymbol("‰"),
        KeyboardSymbol("′"),
        KeyboardSymbol("″")
    )

    val bracketsAndOperators: List<KeyboardSymbol> = listOf(
        KeyboardSymbol("("),
        KeyboardSymbol(")"),
        KeyboardSymbol("["),
        KeyboardSymbol("]"),
        KeyboardSymbol("{"),
        KeyboardSymbol("}"),
        KeyboardSymbol("<"),
        KeyboardSymbol(">"),
        KeyboardSymbol("+"),
        KeyboardSymbol("-"),
        KeyboardSymbol("×"),
        KeyboardSymbol("÷"),
        KeyboardSymbol("="),
        KeyboardSymbol("≠"),
        KeyboardSymbol("±"),
        KeyboardSymbol("≤"),
        KeyboardSymbol("≥"),
        KeyboardSymbol("≈"),
        KeyboardSymbol("∼"),
        KeyboardSymbol("∞")
    )

    val arabicPunctuation: List<KeyboardSymbol> = listOf(
        KeyboardSymbol("،"),
        KeyboardSymbol("؛"),
        KeyboardSymbol("؟"),
        KeyboardSymbol("ـ"),
        KeyboardSymbol("٪"),
        KeyboardSymbol("٫"),
        KeyboardSymbol("٬"),
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
        KeyboardSymbol("•"),
        KeyboardSymbol("§"),
        KeyboardSymbol("©"),
        KeyboardSymbol("®"),
        KeyboardSymbol("™"),
        KeyboardSymbol("°")
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
        KeyboardSymbol("ٱ"),
        KeyboardSymbol("ٓ"),
        KeyboardSymbol("ٔ"),
        KeyboardSymbol("ٕ")
    )

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

    val commonSymbols: List<KeyboardSymbol> = listOf(
        KeyboardSymbol("!"),
        KeyboardSymbol("@"),
        KeyboardSymbol("#"),
        KeyboardSymbol("$"),
        KeyboardSymbol("%"),
        KeyboardSymbol("&"),
        KeyboardSymbol("*"),
        KeyboardSymbol("+"),
        KeyboardSymbol("-"),
        KeyboardSymbol("="),
        KeyboardSymbol("/"),
        KeyboardSymbol("\\"),
        KeyboardSymbol("|"),
        KeyboardSymbol("_"),
        KeyboardSymbol("~"),
        KeyboardSymbol("^"),
        KeyboardSymbol("°"),
        KeyboardSymbol("•"),
        KeyboardSymbol("…"),
        KeyboardSymbol("©"),
        KeyboardSymbol("®"),
        KeyboardSymbol("™")
    )

    val allEnglish: List<KeyboardSymbol> =
        (englishPunctuation + englishExtended + quotationAndTypography)
            .distinctBy { it.outputText }

    val allArabic: List<KeyboardSymbol> =
        (arabicPunctuation + arabicDiacritics + arabicNumerals)
            .distinctBy { it.outputText }

    val all: List<KeyboardSymbol> =
        (allEnglish + allArabic + bracketsAndOperators + commonSymbols)
            .distinctBy { it.outputText }
}
