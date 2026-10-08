package com.keyboardx.app.ime

/**
 * Phase 6 design configuration for Keyboard X.
 *
 * This file contains visual design settings that are independent
 * from keyboard behavior and layout.
 *
 * IMPORTANT:
 * - Does not change key width.
 * - Does not change key height.
 * - Does not change keyboard height.
 * - Does not change row count.
 * - Does not change keyboard layout.
 *
 * These values are prepared for the final Phase 6 connection
 * with KeyboardInputView.
 */
data class KeyboardDesignConfig(
    val themeMode: KeyboardThemeMode = KeyboardThemeMode.LIGHT,

    val keyCornerRadiusDp: Float = 5f,
    val keyVisualInsetDp: Float = 1.5f,
    val keyStrokeWidthDp: Float = 1f,

    val actionTextSizeDp: Float = 13f,
    val spaceTextSizeDp: Float = 12f,
    val multiCharacterTextSizeDp: Float = 16f,
    val singleCharacterTextSizeDp: Float = 18f,

    val variantTextSizeDp: Float = 22f,
    val variantCornerRadiusDp: Float = 7f,
    val variantHeightDp: Float = 58f,
    val variantWidthDp: Float = 54f,
    val variantGapDp: Float = 3f,
    val variantPopupPaddingDp: Float = 4f
) {
    companion object {

        fun default(): KeyboardDesignConfig {
            return KeyboardDesignConfig()
        }

        fun forTheme(
            themeMode: KeyboardThemeMode
        ): KeyboardDesignConfig {
            return KeyboardDesignConfig(
                themeMode = themeMode
            )
        }
    }
}
