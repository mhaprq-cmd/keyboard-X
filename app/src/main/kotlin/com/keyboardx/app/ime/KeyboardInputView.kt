package com.keyboardx.app.ime

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import kotlin.math.max

class KeyboardInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var onKeyboardActionListener: OnKeyboardActionListener? = null

    private var keyboardLayout = KeyboardLayoutProvider.getEnglishLayout()
    private var shiftEnabled = false
    private var pressedKeyIndex = -1

    private val keyBounds = mutableListOf<RectF>()
    private val keyReferences = mutableListOf<Key>()

    private val symbolOutputByLabel: Map<String, String> =
        KeyboardSymbols.all.associate { it.label to it.outputText }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFE0E0E0.toInt()
        style = Paint.Style.FILL
    }

    private val keyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFAFAFA.toInt()
        style = Paint.Style.FILL
    }

    private val pressedKeyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFD6D6D6.toInt()
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF212121.toInt()
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }

    private val keyStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFCCCCCC.toInt()
        style = Paint.Style.STROKE
        strokeWidth = dp(1f)
    }

    init {
        setBackgroundColor(0xFFE0E0E0.toInt())
        isFocusable = true
        isClickable = true
    }

    interface OnKeyboardActionListener {
        fun onKeyPress(keyCode: Int)
        fun onText(text: String)
        fun onDelete()
        fun onSpace()
        fun onEnter()
    }

    fun setOnKeyboardActionListener(listener: OnKeyboardActionListener?) {
        onKeyboardActionListener = listener
    }

    fun clearFocusAndRefresh() {
        clearFocus()
        pressedKeyIndex = -1
        invalidate()
    }

    fun resetState() {
        shiftEnabled = false
        pressedKeyIndex = -1
        invalidate()
    }

    fun setKeyboardLayout(layout: KeyboardLayout) {
        keyboardLayout = layout
        shiftEnabled = false
        pressedKeyIndex = -1
        rebuildKeyBounds()
        invalidate()
    }

    fun getAvailableSymbols(): List<KeyboardSymbol> {
        return KeyboardSymbols.all
    }

    fun insertSymbol(symbol: KeyboardSymbol) {
        if (symbol.outputText.isNotEmpty()) {
            onKeyboardActionListener?.onText(symbol.outputText)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val desiredHeight = dp(280f).toInt()

        val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
            MeasureSpec.AT_MOST -> minOf(
                desiredHeight,
                MeasureSpec.getSize(heightMeasureSpec)
            )
            else -> desiredHeight
        }

        setMeasuredDimension(width, max(dp(220f).toInt(), height))
    }

    override fun onSizeChanged(
        width: Int,
        height: Int,
        oldWidth: Int,
        oldHeight: Int
    ) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        rebuildKeyBounds()
    }

    private fun rebuildKeyBounds() {
        keyBounds.clear()
        keyReferences.clear()

        if (width <= 0 || height <= 0 || keyboardLayout.rows.isEmpty()) {
            return
        }

        val horizontalPadding = dp(4f)
        val verticalPadding = dp(4f)
        val keyGap = dp(4f)

        val availableWidth = width.toFloat() - horizontalPadding * 2
        val availableHeight = height.toFloat() - verticalPadding * 2

        val rowCount = keyboardLayout.rows.size
        val rowHeight =
            (availableHeight - keyGap * (rowCount - 1)) / rowCount

        var top = verticalPadding

        keyboardLayout.rows.forEach { row ->
            val keyCount = row.keys.size

            if (keyCount > 0) {
                val keyWidth =
                    (availableWidth - keyGap * (keyCount - 1)) / keyCount

                var left = horizontalPadding

                row.keys.forEach { key ->
                    val rect = RectF(
                        left,
                        top,
                        left + keyWidth,
                        top + rowHeight
                    )

                    keyBounds.add(rect)
                    keyReferences.add(key)

                    left += keyWidth + keyGap
                }
            }

            top += rowHeight + keyGap
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            backgroundPaint
        )

        if (keyBounds.size != keyReferences.size) {
            rebuildKeyBounds()
        }

        keyBounds.forEachIndexed { index, rect ->
            val key = keyReferences[index]
            val isPressed = index == pressedKeyIndex

            canvas.drawRoundRect(
                rect,
                dp(6f),
                dp(6f),
                if (isPressed) pressedKeyPaint else keyPaint
            )

            canvas.drawRoundRect(
                rect,
                dp(6f),
                dp(6f),
                keyStrokePaint
            )

            val label = getDisplayLabel(key)

            textPaint.textSize = when {
                key.code == KeyEvent.KEYCODE_SPACE -> dp(14f)
                label.length > 1 -> dp(17f)
                else -> dp(20f)
            }

            val fontMetrics = textPaint.fontMetrics
            val textCenterY =
                rect.centerY() -
                    (fontMetrics.ascent + fontMetrics.descent) / 2f

            canvas.drawText(
                label,
                rect.centerX(),
                textCenterY,
                textPaint
            )
        }
    }

    private fun getDisplayLabel(key: Key): String {
        if (key.code in KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z) {
            return if (shiftEnabled) {
                key.label.uppercase()
            } else {
                key.label.lowercase()
            }
        }

        return key.label
    }

    private fun getSymbolOutput(key: Key): String? {
        return symbolOutputByLabel[key.label]
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                pressedKeyIndex = findKeyAt(event.x, event.y)
                invalidate()
                return pressedKeyIndex >= 0
            }

            MotionEvent.ACTION_MOVE -> {
                val newIndex = findKeyAt(event.x, event.y)

                if (newIndex != pressedKeyIndex) {
                    pressedKeyIndex = newIndex
                    invalidate()
                }

                return true
            }

            MotionEvent.ACTION_UP -> {
                val releasedIndex = findKeyAt(event.x, event.y)
                val pressedIndex = pressedKeyIndex

                pressedKeyIndex = -1
                invalidate()

                if (
                    pressedIndex >= 0 &&
                    pressedIndex == releasedIndex
                ) {
                    handleKey(keyReferences[pressedIndex])
                }

                performClick()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                pressedKeyIndex = -1
                invalidate()
                return true
            }
        }

        return true
    }

    private fun findKeyAt(x: Float, y: Float): Int {
        for (index in keyBounds.indices) {
            if (keyBounds[index].contains(x, y)) {
                return index
            }
        }

        return -1
    }

    private fun handleKey(key: Key) {
        when (key.code) {
            KeyEvent.KEYCODE_SHIFT_LEFT -> {
                shiftEnabled = !shiftEnabled
                invalidate()
            }

            KeyEvent.KEYCODE_DEL -> {
                onKeyboardActionListener?.onDelete()
            }

            KeyEvent.KEYCODE_SPACE -> {
                onKeyboardActionListener?.onSpace()
            }

            KeyEvent.KEYCODE_ENTER -> {
                onKeyboardActionListener?.onEnter()
            }

            else -> {
                val output =
                    key.outputText
                        ?: getSymbolOutput(key)
                        ?: getDisplayLabel(key)

                if (output.isNotEmpty()) {
                    onKeyboardActionListener?.onText(output)

                    if (
                        shiftEnabled &&
                        key.code in KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z
                    ) {
                        shiftEnabled = false
                        invalidate()
                    }
                } else {
                    onKeyboardActionListener?.onKeyPress(key.code)
                }
            }
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun dp(value: Float): Float {
        return value * resources.displayMetrics.density
    }
}
