package com.keyboardx.app.ime

/**
 * Phase 6 background system for Keyboard X.
 *
 * Defines the available keyboard background types
 * and their animation configuration.
 *
 * IMPORTANT:
 * - Backgrounds are local resources bundled with the APK/AAB.
 * - No internet connection is required at runtime.
 * - This file does not draw or play the background.
 * - Playback and rendering will be handled by the background manager
 *   and connected to KeyboardInputView at the end of Phase 6.
 * - This file does not change keyboard dimensions or key dimensions.
 */
enum class KeyboardBackgroundType {
    NONE,
    STATIC,
    ANIMATED
}

/**
 * Animation behavior for an animated keyboard background.
 */
enum class KeyboardBackgroundAnimationMode {
    LOOP,
    PING_PONG,
    ONCE
}

/**
 * Describes a Keyboard X background.
 *
 * For a static background:
 * - resourceIds should contain one drawable resource ID.
 *
 * For an animated background:
 * - resourceIds contains the animation frames in their display order.
 * - frameDurationMs controls the duration of each frame.
 * - animationMode controls how the animation repeats.
 *
 * Resource IDs are used so backgrounds remain packaged locally
 * inside the application without requiring network access.
 */
data class KeyboardBackground(
    val type: KeyboardBackgroundType,
    val resourceIds: List<Int> = emptyList(),
    val animationMode: KeyboardBackgroundAnimationMode =
        KeyboardBackgroundAnimationMode.LOOP,
    val frameDurationMs: Long = 100L
) {

    /**
     * Returns true when this background has usable resources.
     */
    fun isValid(): Boolean {
        return when (type) {
            KeyboardBackgroundType.NONE -> true

            KeyboardBackgroundType.STATIC -> {
                resourceIds.size == 1
            }

            KeyboardBackgroundType.ANIMATED -> {
                resourceIds.isNotEmpty() &&
                    frameDurationMs > 0L
            }
        }
    }

    companion object {

        /**
         * Creates a background with no visual background asset.
         */
        fun none(): KeyboardBackground {
            return KeyboardBackground(
                type = KeyboardBackgroundType.NONE
            )
        }

        /**
         * Creates a static local background.
         */
        fun static(
            resourceId: Int
        ): KeyboardBackground {
            return KeyboardBackground(
                type = KeyboardBackgroundType.STATIC,
                resourceIds = listOf(resourceId)
            )
        }

        /**
         * Creates an animated local background.
         *
         * @param resourceIds animation frames in display order.
         * @param animationMode playback mode.
         * @param frameDurationMs duration of each frame in milliseconds.
         */
        fun animated(
            resourceIds: List<Int>,
            animationMode: KeyboardBackgroundAnimationMode =
                KeyboardBackgroundAnimationMode.LOOP,
            frameDurationMs: Long = 100L
        ): KeyboardBackground {
            return KeyboardBackground(
                type = KeyboardBackgroundType.ANIMATED,
                resourceIds = resourceIds,
                animationMode = animationMode,
                frameDurationMs = frameDurationMs
            )
        }
    }
}
