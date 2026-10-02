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
 * This is a basic implementation for Phase 3.
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
    }

    /**
     * Callback interface for keyboard actions.
     * Implemented by KeyboardIMEService to handle key presses.
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
     */
    fun setOnKeyboardActionListener(listener: OnKeyboardActionListener?) {
        onKeyboardActionListener = listener
    }

    /**
     * Clear view focus and reset state.
     */
    fun clearFocus() {
        super.clearFocus()
        invalidate()
    }

    /**
     * Reset keyboard state.
     * Can be called when input view is restarted.
     */
    fun resetState() {
        invalidate()
    }

    @Deprecated("Use onKeyPress directly from external handlers")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Placeholder for Phase 4 keyboard layout drawing
        // Currently shows a simple background indicating IME is active
        paint.textSize = 14f
        paint.color = 0xFF666666.toInt()
        canvas.drawText("Keyboard X IME Active", 16f, 30f, paint)
    }
}
