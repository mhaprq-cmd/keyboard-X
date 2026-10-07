package com.keyboardx.app.ime

/**
 * Phase 6 design system for Keyboard X.
 *
 * This file contains visual configuration only.
 * KeyboardInputView remains responsible for keyboard behavior and drawing.
 *
 * IMPORTANT:
 * - Current values intentionally match the existing KeyboardInputView.
 * - Key width, row height, keyboard height, and key spacing are NOT changed here.
 * - This file is prepared for the final Phase 6 connection so future visual
 *   changes can be made without repeatedly modifying KeyboardInputView.
 */
enum class KeyboardThemeMode {
    LIGHT,
    DARK
}

data class KeyboardTheme(
    val backgroundColor: Int,
    val keyColor: Int,
    val pressedKeyColor: Int,
    val textColor: Int,
    val keyStrokeColor: Int,
    val variantBackgroundColor: Int,
    val variantSelectedColor: Int,
    val variantStrokeColor: Int,

    val keyCornerRadiusDp: Float,
    val keyVisualInsetDp: Float,
    val keyStrokeWidthDp: Float,

    val actionTextSizeDp: Float,
    val spaceTextSizeDp: Float,
    val multiCharacterTextSizeDp: Float,
    val singleCharacterTextSizeDp: Float,
    val variantTextSizeDp: Float,

    val variantCornerRadiusDp: Float,
    val variantHeightDp: Float,
    val variantWidthDp: Float,
    val variantGapDp: Float,
    val variantPopupPaddingDp: Float
)

object KeyboardThemes {

    /**
     * Existing Keyboard X appearance.
     *
     * These values are copied from the current KeyboardInputView so that
     * moving the visual configuration into this file does not change the UI.
     */
    val LIGHT = KeyboardTheme(
        backgroundColor = 0xFFE0E0E0.toInt(),
        keyColor = 0xFFFAFAFA.toInt(),
        pressedKeyColor = 0xFFD6D6D6.toInt(),
        textColor = 0xFF212121.toInt(),
        keyStrokeColor = 0xFFCCCCCC.toInt(),
        variantBackgroundColor = 0xFFFFFFFF.toInt(),
        variantSelectedColor = 0xFFD6D6D6.toInt(),
        variantStrokeColor = 0xFFAAAAAA.toInt(),

        keyCornerRadiusDp = 5f,
        keyVisualInsetDp = 1.5f,
        keyStrokeWidthDp = 1f,

        actionTextSizeDp = 13f,
        spaceTextSizeDp = 12f,
        multiCharacterTextSizeDp = 16f,
        singleCharacterTextSizeDp = 18f,
        variantTextSizeDp = 22f,

        variantCornerRadiusDp = 7f,
        variantHeightDp = 58f,
        variantWidthDp = 54f,
        variantGapDp = 3f,
        variantPopupPaddingDp = 4f
    )

    /**
     * Dark theme configuration.
     *
     * It is defined now as part of the Phase 6 architecture, but it is not
     * connected to KeyboardInputView yet. Therefore it cannot change the
     * current appearance until the final theme integration step.
     */
    val DARK = KeyboardTheme(
        backgroundColor = 0xFF202124.toInt(),
        keyColor = 0xFF303134.toInt(),
        pressedKeyColor = 0xFF45464A.toInt(),
        textColor = 0xFFF1F3F4.toInt(),
        keyStrokeColor = 0xFF5F6368.toInt(),
        variantBackgroundColor = 0xFF303134.toInt(),
        variantSelectedColor = 0xFF45464A.toInt(),
        variantStrokeColor = 0xFF5F6368.toInt(),

        keyCornerRadiusDp = 5f,
        keyVisualInsetDp = 1.5f,
        keyStrokeWidthDp = 1f,

        actionTextSizeDp = 13f,
        spaceTextSizeDp = 12f,
        multiCharacterTextSizeDp = 16f,
        singleCharacterTextSizeDp = 18f,
        variantTextSizeDp = 22f,

        variantCornerRadiusDp = 7f,
        variantHeightDp = 58f,
        variantWidthDp = 54f,
        variantGapDp = 3f,
        variantPopupPaddingDp = 4f
    )

    fun get(mode: KeyboardThemeMode): KeyboardTheme {
        return when (mode) {
            KeyboardThemeMode.LIGHT -> LIGHT
            KeyboardThemeMode.DARK -> DARK
        }
    }
}
