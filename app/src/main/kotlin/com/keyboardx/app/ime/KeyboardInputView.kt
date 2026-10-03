package com.keyboardx.app.ime

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Custom Input View for the Keyboard X IME.
 * Handles keyboard input display and touch events.
 *
 * Phase 3: Real, buildable implementation for IME integration.
 * Advanced keyboard layout and styling will be added in Phase 4.
 */
class KeyboardInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var onKeyboardActionListener: OnKeyboardActionListener? = null

    private val paint = Paint().apply {
        color = 0xFF000000.toInt()
        isAntiAlias = true
    }

    init {
        setBackgroundColor(0xFFE0E0E0.toInt())
        isFocusable = true
        isClickable = true
    }

    /**
     * Callback interface for keyboard actions.
     * Implemented by KeyboardIMEService to handle key presses and text input.
     */
    interface OnKeyboardActionListener {
        fun onKeyPress(keyCode: Int)
        fun onText(text: String)
        fun onDelete()
        fun onSpace()
        fun onEnter()
    }

    /**
     * Set the keyboard action listener.
     * Called by KeyboardIMEService during onCreateInputView.
     */
    fun setOnKeyboardActionListener(listener: OnKeyboardActionListener?) {
        onKeyboardActionListener = listener
    }

    /**
     * Clear view focus and refresh the display.
     * Called when input view is being finished.
     */
    fun clearFocusAndRefresh() {
        clearFocus()
        invalidate()
    }

    /**
     * Reset keyboard state.
     * Called when input view is restarted without changing the target field.
     */
    fun resetState() {
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isPressed = true
                return true
            }
            MotionEvent.ACTION_UP -> {
                isPressed = false
                val x = event.x
                when {
                    x < width * 0.25f -> onKeyboardActionListener?.onDelete()
                    x in (width * 0.25f)..(width * 0.75f) -> onKeyboardActionListener?.onSpace()
                    else -> onKeyboardActionListener?.onEnter()
                }
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.textSize = 14f
        paint.color = 0xFF666666.toInt()
        canvas.drawText("Keyboard X IME Active", 16f, 30f, paint)
    }
}
