package com.keyboardx.app.ime

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

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

    private val deleteHandler = Handler(Looper.getMainLooper())

    private var deletePressed = false
    private var deleteKeyIndex = -1

    private val deleteRepeatRunnable = object : Runnable {
        override fun run() {
            if (!deletePressed) {
                return
            }

            onKeyboardActionListener?.onDelete()
            deleteHandler.postDelayed(this, 65L)
        }
    }

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
        stopDeleteRepeat()
        clearFocus()
        pressedKeyIndex = -1
        invalidate()
    }

    fun resetState() {
        stopDeleteRepeat()
        keyboardLayout = KeyboardLayoutProvider.getEnglishLayout()
        shiftEnabled = false
        pressedKeyIndex = -1
        rebuildKeyBounds()
        invalidate()
    }

    fun setKeyboardLayout(layout: KeyboardLayout) {
        stopDeleteRepeat()
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

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        val width = MeasureSpec.getSize(widthMeasureSpec)

        val screenHeight = resources.displayMetrics.heightPixels.toFloat()
        val responsiveHeight = screenHeight * 0.36f

        val targetHeight = min(
            dp(245f),
            responsiveHeight
        ).toInt()

        val minimumHeight = dp(200f).toInt()

        val availableHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY,
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(heightMeasureSpec)

            else -> targetHeight
        }

        val height = max(
            minimumHeight,
            min(targetHeight, availableHeight)
        )

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(
        width: Int,
        height: Int,
        oldWidth: Int,
        oldHeight: Int
    ) {
        super.onSizeChanged(
            width,
            height,
            oldWidth,
            oldHeight
        )

        rebuildKeyBounds()
    }

    private fun rebuildKeyBounds() {
        keyBounds.clear()
        keyReferences.clear()

        if (
            width <= 0 ||
            height <= 0 ||
            keyboardLayout.rows.isEmpty()
        ) {
            return
        }

        val horizontalPadding = dp(4f)
        val verticalPadding = dp(4f)
        val keyGap = dp(3f)

        val availableWidth =
            width.toFloat() - horizontalPadding * 2f

        val availableHeight =
            height.toFloat() - verticalPadding * 2f

        val rowCount = keyboardLayout.rows.size

        val rowHeight =
            (availableHeight - keyGap * (rowCount - 1)) / rowCount

        var top = verticalPadding

        keyboardLayout.rows.forEach { row ->
            val keyCount = row.keys.size

            if (keyCount > 0) {
                val keyWidth =
                    (availableWidth - keyGap * (keyCount - 1)) /
                        keyCount

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
                if (isPressed) {
                    pressedKeyPaint
                } else {
                    keyPaint
                }
            )

            canvas.drawRoundRect(
                rect,
                dp(6f),
                dp(6f),
                keyStrokePaint
            )

            val label = getDisplayLabel(key)

            textPaint.textSize = when {
                key.action != KeyAction.NONE &&
                    label.length > 2 -> dp(14f)

                key.code == KeyEvent.KEYCODE_SPACE -> dp(13f)

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
        if (
            key.code in
            KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z
        ) {
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
                pressedKeyIndex =
                    findKeyAt(event.x, event.y)

                if (pressedKeyIndex >= 0) {
                    val key = keyReferences[pressedKeyIndex]

                    if (key.action == KeyAction.DELETE) {
                        startDeleteRepeat(
                            pressedKeyIndex
                        )
                    }
                }

                invalidate()

                return pressedKeyIndex >= 0
            }

            MotionEvent.ACTION_MOVE -> {
                val newIndex =
                    findKeyAt(event.x, event.y)

                if (deletePressed) {
                    if (newIndex != deleteKeyIndex) {
                        stopDeleteRepeat()
                    }
                }

                if (newIndex != pressedKeyIndex) {
                    pressedKeyIndex = newIndex
                    invalidate()
                }

                return true
            }

            MotionEvent.ACTION_UP -> {
                val releasedIndex =
                    findKeyAt(event.x, event.y)

                val pressedIndex =
                    pressedKeyIndex

                val wasDeletePressed =
                    deletePressed

                stopDeleteRepeat()

                pressedKeyIndex = -1
                invalidate()

                if (
                    pressedIndex >= 0 &&
                    pressedIndex == releasedIndex
                ) {
                    val key =
                        keyReferences[pressedIndex]

                    if (
                        key.action !=
                        KeyAction.DELETE ||
                        !wasDeletePressed
                    ) {
                        handleKey(key)
                    }
                }

                performClick()

                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                stopDeleteRepeat()

                pressedKeyIndex = -1
                invalidate()

                return true
            }
        }

        return true
    }

    private fun startDeleteRepeat(index: Int) {
        stopDeleteRepeat()

        deletePressed = true
        deleteKeyIndex = index

        onKeyboardActionListener?.onDelete()

        deleteHandler.postDelayed(
            deleteRepeatRunnable,
            350L
        )
    }

    private fun stopDeleteRepeat() {
        deletePressed = false
        deleteKeyIndex = -1

        deleteHandler.removeCallbacks(
            deleteRepeatRunnable
        )
    }

    private fun findKeyAt(
        x: Float,
        y: Float
    ): Int {
        for (index in keyBounds.indices) {
            if (keyBounds[index].contains(x, y)) {
                return index
            }
        }

        return -1
    }

    private fun handleKey(key: Key) {
        when (key.action) {

            KeyAction.SHIFT -> {
                shiftEnabled = !shiftEnabled
                invalidate()
            }

            KeyAction.DELETE -> {
                onKeyboardActionListener?.onDelete()
            }

            KeyAction.SPACE -> {
                onKeyboardActionListener?.onSpace()
            }

            KeyAction.ENTER -> {
                onKeyboardActionListener?.onEnter()
            }

            KeyAction.SWITCH_TO_ENGLISH -> {
                setKeyboardLayout(
                    KeyboardLayoutProvider
                        .getEnglishLayout()
                )
            }

            KeyAction.SWITCH_TO_NUMBERS -> {
                setKeyboardLayout(
                    KeyboardLayoutProvider
                        .getNumbersLayout()
                )
            }

            KeyAction.SWITCH_TO_SYMBOLS -> {
                setKeyboardLayout(
                    KeyboardLayoutProvider
                        .getSymbolsLayout()
                )
            }

            KeyAction.SWITCH_LANGUAGE -> {
                setKeyboardLayout(
                    KeyboardLayoutProvider
                        .getEnglishLayout()
                )
            }

            KeyAction.NONE -> {
                val output =
                    key.outputText
                        ?: getSymbolOutput(key)
                        ?: getDisplayLabel(key)

                if (output.isNotEmpty()) {
                    onKeyboardActionListener?.onText(
                        output
                    )

                    if (
                        shiftEnabled &&
                        key.code in
                        KeyEvent.KEYCODE_A..
                        KeyEvent.KEYCODE_Z
                    ) {
                        shiftEnabled = false
                        invalidate()
                    }
                } else {
                    onKeyboardActionListener?.onKeyPress(
                        key.code
                    )
                }
            }
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun dp(value: Float): Float {
        return value *
            resources.displayMetrics.density
    }
}
