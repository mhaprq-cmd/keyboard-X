package com.keyboardx.app.ime

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Custom keyboard view for rendering keyboard layout.
 * This is a placeholder for Phase 1.
 * Advanced keyboard logic will be added in future phases.
 */
class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnKeyboardActionListener {
        fun onKey(primaryCode: Int, keyCodes: IntArray?)
        fun onText(text: CharSequence?)
        fun swipeLeft()
        fun swipeRight()
        fun swipeUp()
        fun swipeDown()
    }

    private var onKeyboardActionListener: OnKeyboardActionListener? = null

    fun setOnKeyboardActionListener(listener: OnKeyboardActionListener?) {
        onKeyboardActionListener = listener
    }
}