package com.keyboardx.app.ime

import android.graphics.drawable.Drawable

/**
 * Phase 6 background manager for Keyboard X.
 *
 * Responsible for managing the active keyboard background
 * and preparing the background drawable for rendering.
 *
 * IMPORTANT:
 * - Background resources are local and bundled with the APK/AAB.
 * - No internet connection is required.
 * - Does not change keyboard height.
 * - Does not change key width or key height.
 * - Does not change keyboard layout or typing behavior.
 * - KeyboardInputView will be connected to this manager
 *   at the end of Phase 6.
 */
class KeyboardBackgroundManager {

    private var currentBackground: KeyboardBackground =
        KeyboardBackground.none()

    private var currentFrameIndex: Int = 0

    private var animationDirection: Int = 1

    private var animationFinished: Boolean = false

    /**
     * Sets the active background.
     */
    fun setBackground(
        background: KeyboardBackground
    ) {
        currentBackground = background

        resetAnimation()
    }

    /**
     * Returns the currently active background.
     */
    fun getBackground(): KeyboardBackground {
        return currentBackground
    }

    /**
     * Resets the animation to its initial state.
     */
    fun resetAnimation() {
        currentFrameIndex = 0
        animationDirection = 1
        animationFinished = false
    }

    /**
     * Returns the current frame resource ID.
     *
     * Returns null when there is no drawable resource
     * available for the current background.
     */
    fun getCurrentResourceId(): Int? {
        return when (currentBackground.type) {
            KeyboardBackgroundType.NONE -> null

            KeyboardBackgroundType.STATIC -> {
                currentBackground.resourceIds.firstOrNull()
            }

            KeyboardBackgroundType.ANIMATED -> {
                currentBackground.resourceIds.getOrNull(
                    currentFrameIndex
                )
            }
        }
    }

    /**
     * Advances the animation by one frame.
     *
     * Returns true when the frame changed.
     */
    fun advanceFrame(): Boolean {
        if (currentBackground.type != KeyboardBackgroundType.ANIMATED) {
            return false
        }

        val frameCount = currentBackground.resourceIds.size

        if (frameCount <= 1 || animationFinished) {
            return false
        }

        when (currentBackground.animationMode) {

            KeyboardBackgroundAnimationMode.LOOP -> {
                currentFrameIndex =
                    (currentFrameIndex + 1) % frameCount
            }

            KeyboardBackgroundAnimationMode.PING_PONG -> {
                advancePingPong(frameCount)
            }

            KeyboardBackgroundAnimationMode.ONCE -> {
                if (currentFrameIndex < frameCount - 1) {
                    currentFrameIndex++
                } else {
                    animationFinished = true
                }
            }
        }

        return true
    }

    /**
     * Handles PING_PONG animation.
     *
     * The frames move forward to the last frame,
     * then backward to the first frame, continuously.
     */
    private fun advancePingPong(
        frameCount: Int
    ) {
        if (frameCount <= 1) {
            return
        }

        val nextIndex =
            currentFrameIndex + animationDirection

        if (nextIndex >= frameCount) {
            animationDirection = -1
            currentFrameIndex = frameCount - 2
        } else if (nextIndex < 0) {
            animationDirection = 1
            currentFrameIndex = 1
        } else {
            currentFrameIndex = nextIndex
        }
    }

    /**
     * Returns the configured frame duration.
     */
    fun getFrameDurationMs(): Long {
        return currentBackground.frameDurationMs
    }

    /**
     * Returns true when the current background is animated.
     */
    fun isAnimated(): Boolean {
        return currentBackground.type ==
            KeyboardBackgroundType.ANIMATED
    }

    /**
     * Returns true when an ONCE animation has reached
     * its final frame.
     */
    fun isAnimationFinished(): Boolean {
        return animationFinished
    }

    /**
     * Returns true when a usable background resource exists.
     */
    fun hasBackground(): Boolean {
        return getCurrentResourceId() != null
    }

    /**
     * Clears the active background.
     */
    fun clearBackground() {
        currentBackground = KeyboardBackground.none()
        resetAnimation()
    }
}
