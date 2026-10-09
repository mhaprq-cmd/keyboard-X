package com.keyboardx.app.ime

import com.keyboardx.app.R

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    private var activeLanguage = KeyboardLayout.Language.ENGLISH
    private var shiftEnabled = false
    private var pressedKeyIndex = -1

    private val preferences =
        context.getSharedPreferences(
            "keyboard_x_preferences",
            Context.MODE_PRIVATE
        )

    private val keyBounds = mutableListOf<RectF>()
    private val keyReferences = mutableListOf<Key>()

    private val symbolOutputByLabel: Map<String, String> =
        KeyboardSymbols.all.associate { it.label to it.outputText }

    private val deleteHandler = Handler(Looper.getMainLooper())
    private val longPressHandler = Handler(Looper.getMainLooper())

    private var deletePressed = false
    private var deleteKeyIndex = -1

    private var longPressPending = false
    private var longPressTriggered = false
    private var longPressKeyIndex = -1
    private var longPressVariants = emptyList<String>()
    private var selectedVariantIndex = 0

    /*
     * Phase 6 visual system.
     * Theme selection follows the Android system light/dark mode.
     */
    private var activeTheme: KeyboardTheme =
        KeyboardThemeManager.getCurrentTheme(context)

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val keyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val pressedKeyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }

    private val keyStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val variantBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val variantSelectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val variantStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val variantBounds = mutableListOf<RectF>()

    /*
     * Static background image already present in the repository:
     * res/drawable/keyboard_background_static.jpg
     *
     * The bitmap is loaded locally; no internet connection is used.
     */
    private var staticBackgroundBitmap: Bitmap? = null

    private val backgroundManager = KeyboardBackgroundManager()

    private val backgroundDestination = RectF()

    init {
        activeLanguage = loadPreferredLanguage()

        keyboardLayout =
            if (activeLanguage == KeyboardLayout.Language.ARABIC) {
                KeyboardLayoutProvider.getArabicLayout()
            } else {
                KeyboardLayoutProvider.getEnglishLayout()
            }

        staticBackgroundBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.keyboard_background_static
        )

        backgroundManager.setBackground(
            KeyboardBackground.static(
                R.drawable.keyboard_background_static
            )
        )

        applyTheme(activeTheme)

        isFocusable = true
        isClickable = true
    }

    interface OnKeyboardActionListener {
        fun onKeyPress(keyCode: Int)
        fun onText(text: String)
        fun onDelete()
        fun onSpace()
        fun onEnter()
        fun onSearch()
    }

    fun setOnKeyboardActionListener(
        listener: OnKeyboardActionListener?
    ) {
        onKeyboardActionListener = listener
    }

    private fun applyTheme(theme: KeyboardTheme) {
        activeTheme = theme

        backgroundPaint.color = theme.backgroundColor
        keyPaint.color = theme.keyColor
        pressedKeyPaint.color = theme.pressedKeyColor
        textPaint.color = theme.textColor
        keyStrokePaint.color = theme.keyStrokeColor
        keyStrokePaint.strokeWidth = dp(theme.keyStrokeWidthDp)

        variantBackgroundPaint.color = theme.variantBackgroundColor
        variantSelectedPaint.color = theme.variantSelectedColor
        variantStrokePaint.color = theme.variantStrokeColor
        variantStrokePaint.strokeWidth = dp(theme.keyStrokeWidthDp)

        setBackgroundColor(theme.backgroundColor)
        invalidate()
    }

    private fun refreshThemeFromSystem() {
        applyTheme(
            KeyboardThemeManager.getCurrentTheme(context)
        )
    }

    fun clearFocusAndRefresh() {
        stopDeleteRepeat()
        stopLongPress()
        clearFocus()
        pressedKeyIndex = -1
        refreshThemeFromSystem()
        invalidate()
    }

    fun resetState() {
        stopDeleteRepeat()
        stopLongPress()

        keyboardLayout =
            if (activeLanguage == KeyboardLayout.Language.ARABIC) {
                KeyboardLayoutProvider.getArabicLayout()
            } else {
                KeyboardLayoutProvider.getEnglishLayout()
            }

        shiftEnabled = false
        pressedKeyIndex = -1

        refreshThemeFromSystem()
        rebuildKeyBounds()
        invalidate()
    }

    fun setKeyboardLayout(layout: KeyboardLayout) {
        stopDeleteRepeat()
        stopLongPress()

        keyboardLayout = layout

        if (layout.mode == KeyboardMode.ENGLISH) {
            activeLanguage = layout.language
            savePreferredLanguage(activeLanguage)
        }

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

        // Preserve the existing keyboard height behavior.
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()
        val responsiveHeight = screenHeight * 0.33f

        val targetHeight = min(
            dp(225f),
            responsiveHeight
        ).toInt()

        val minimumHeight = dp(180f).toInt()

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

    /*
     * Key width weights are applied within each row.
     * Horizontal padding, gaps, row count and keyboard height remain unchanged.
     */
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
            val keys = row.keys
            val keyCount = keys.size

            if (keyCount > 0) {
                val totalGap = keyGap * (keyCount - 1)

                val totalWeight = keys.sumOf { key ->
                    key.widthWeight.coerceAtLeast(0.1f).toDouble()
                }.toFloat()

                val availableKeyWidth =
                    availableWidth - totalGap

                var left = horizontalPadding

                keys.forEach { key ->
                    val weight =
                        key.widthWeight.coerceAtLeast(0.1f)

                    val keyWidth =
                        availableKeyWidth * weight / totalWeight

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

        drawKeyboardBackground(canvas)

        if (keyBounds.size != keyReferences.size) {
            rebuildKeyBounds()
        }

        keyBounds.forEachIndexed { index, bounds ->
            val key = keyReferences[index]
            val isPressed = index == pressedKeyIndex

            val visualInset = dp(activeTheme.keyVisualInsetDp)

            val rect = RectF(
                bounds.left + visualInset,
                bounds.top + visualInset,
                bounds.right - visualInset,
                bounds.bottom - visualInset
            )

            canvas.drawRoundRect(
                rect,
                dp(activeTheme.keyCornerRadiusDp),
                dp(activeTheme.keyCornerRadiusDp),
                if (isPressed) pressedKeyPaint else keyPaint
            )

            canvas.drawRoundRect(
                rect,
                dp(activeTheme.keyCornerRadiusDp),
                dp(activeTheme.keyCornerRadiusDp),
                keyStrokePaint
            )

            val label = getDisplayLabel(key)

            textPaint.textSize = when {
                key.action != KeyAction.NONE &&
                    label.length > 2 ->
                    dp(activeTheme.actionTextSizeDp)

                key.code == KeyEvent.KEYCODE_SPACE ->
                    dp(activeTheme.spaceTextSizeDp)

                label.length > 1 ->
                    dp(activeTheme.multiCharacterTextSizeDp)

                else ->
                    dp(activeTheme.singleCharacterTextSizeDp)
            }

            val fontMetrics = textPaint.fontMetrics

            val textCenterY =
                rect.centerY() -
                    (fontMetrics.ascent + fontMetrics.descent) / 2f

            if (label.isNotEmpty()) {
                canvas.drawText(
                    label,
                    rect.centerX(),
                    textCenterY,
                    textPaint
                )
            }
        }

        drawLongPressVariants(canvas)
    }

    private fun drawKeyboardBackground(canvas: Canvas) {
        canvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            backgroundPaint
        )

        val bitmap = staticBackgroundBitmap ?: return
        if (bitmap.isRecycled || width <= 0 || height <= 0) return

        backgroundDestination.set(
            0f,
            0f,
            width.toFloat(),
            height.toFloat()
        )

        val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isFilterBitmap = true
            alpha = 255
        }

        canvas.drawBitmap(
            bitmap,
            null,
            backgroundDestination,
            imagePaint
        )
    }
      private fun drawLongPressVariants(canvas: Canvas) {
        if (
            !longPressTriggered ||
            longPressKeyIndex !in keyBounds.indices ||
            longPressVariants.isEmpty()
        ) {
            variantBounds.clear()
            return
        }

        val keyRect = keyBounds[longPressKeyIndex]

        // Smaller popup so it covers less of the keyboard.
        val variantHeight = dp(38f)
        val variantWidth = dp(36f)
        val variantGap = dp(2f)
        val popupPadding = dp(3f)

        val totalWidth =
            popupPadding * 2f +
                variantWidth * longPressVariants.size +
                variantGap * (longPressVariants.size - 1)

        var popupLeft =
            keyRect.centerX() - totalWidth / 2f

        popupLeft = max(
            dp(2f),
            min(
                popupLeft,
                width.toFloat() - totalWidth - dp(2f)
            )
        )

        val popupBottom = keyRect.top - dp(3f)
        val popupTop = popupBottom - variantHeight

        variantBounds.clear()

        longPressVariants.forEachIndexed { index, variant ->
            val rect = RectF(
                popupLeft,
                popupTop,
                popupLeft + variantWidth,
                popupBottom
            )

            variantBounds.add(rect)

            canvas.drawRoundRect(
                rect,
                dp(activeTheme.variantCornerRadiusDp),
                dp(activeTheme.variantCornerRadiusDp),
                if (index == selectedVariantIndex) {
                    variantSelectedPaint
                } else {
                    variantBackgroundPaint
                }
            )

            canvas.drawRoundRect(
                rect,
                dp(activeTheme.variantCornerRadiusDp),
                dp(activeTheme.variantCornerRadiusDp),
                variantStrokePaint
            )

            textPaint.textSize = dp(18f)

            val fontMetrics = textPaint.fontMetrics

            val textCenterY =
                rect.centerY() -
                    (fontMetrics.ascent + fontMetrics.descent) / 2f

            canvas.drawText(
                variant,
                rect.centerX(),
                textCenterY,
                textPaint
            )

            popupLeft += variantWidth + variantGap
        }
    }

    private fun getDisplayLabel(key: Key): String {
        if (key.action == KeyAction.SPACE) {
            return ""
        }

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
                pressedKeyIndex = findKeyAt(event.x, event.y)

                if (pressedKeyIndex >= 0) {
                    val key = keyReferences[pressedKeyIndex]

                    if (key.action == KeyAction.DELETE) {
                        startDeleteRepeat(pressedKeyIndex)
                    } else {
                        startLongPress(pressedKeyIndex)
                    }
                }

                invalidate()
                return pressedKeyIndex >= 0
            }

            MotionEvent.ACTION_MOVE -> {
                if (longPressTriggered) {
                    updateVariantSelection(event.x, event.y)
                    invalidate()
                    return true
                }

                val newIndex = findKeyAt(event.x, event.y)

                if (deletePressed && newIndex != deleteKeyIndex) {
                    stopDeleteRepeat()
                }

                if (
                    longPressPending &&
                    newIndex != longPressKeyIndex
                ) {
                    stopLongPress()
                }

                if (newIndex != pressedKeyIndex) {
                    pressedKeyIndex = newIndex
                    invalidate()
                }

                return true
            }

            MotionEvent.ACTION_UP -> {
                if (longPressTriggered) {
                    commitSelectedVariant()

                    stopLongPress()
                    stopDeleteRepeat()

                    pressedKeyIndex = -1
                    invalidate()
                    performClick()

                    return true
                }

                val releasedIndex = findKeyAt(event.x, event.y)
                val pressedIndex = pressedKeyIndex
                val wasDeletePressed = deletePressed

                stopLongPress()
                stopDeleteRepeat()

                pressedKeyIndex = -1
                invalidate()

                if (
                    pressedIndex >= 0 &&
                    pressedIndex == releasedIndex
                ) {
                    val key = keyReferences[pressedIndex]

                    if (
                        key.action != KeyAction.DELETE ||
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
                stopLongPress()

                pressedKeyIndex = -1
                invalidate()

                return true
            }
        }

        return true
    }

    private fun startLongPress(index: Int) {
        stopLongPress()

        if (
            keyboardLayout.language !=
            KeyboardLayout.Language.ARABIC
        ) {
            return
        }

        val key = keyReferences.getOrNull(index) ?: return
        val character = key.outputText ?: key.label

        if (!ArabicKeyboard.hasLongPressVariants(character)) {
            return
        }

        longPressPending = true
        longPressTriggered = false
        longPressKeyIndex = index
        longPressVariants = emptyList()
        selectedVariantIndex = 0

        longPressHandler.postDelayed(
            longPressRunnable,
            450L
        )
    }

    private val longPressRunnable = object : Runnable {
        override fun run() {
            if (
                pressedKeyIndex < 0 ||
                pressedKeyIndex != longPressKeyIndex
            ) {
                return
            }

            val key = keyReferences.getOrNull(longPressKeyIndex)
                ?: return

            if (
                keyboardLayout.language !=
                KeyboardLayout.Language.ARABIC
            ) {
                return
            }

            val character = key.outputText ?: key.label
            val variants =
                ArabicKeyboard.getLongPressVariants(character)

            if (variants.isEmpty()) {
                return
            }

            longPressPending = false
            longPressTriggered = true
            longPressVariants = variants
            selectedVariantIndex = 0

            invalidate()
        }
    }

    private val deleteRepeatRunnable = object : Runnable {
        override fun run() {
            if (!deletePressed) {
                return
            }

            onKeyboardActionListener?.onDelete()
            deleteHandler.postDelayed(this, 65L)
        }
    }

    private fun stopLongPress() {
        longPressPending = false
        longPressTriggered = false
        longPressKeyIndex = -1
        longPressVariants = emptyList()
        selectedVariantIndex = 0

        longPressHandler.removeCallbacks(longPressRunnable)
        variantBounds.clear()
    }

    private fun updateVariantSelection(
        x: Float,
        y: Float
    ) {
        if (variantBounds.isEmpty()) {
            return
        }

        val index = variantBounds.indexOfFirst {
            it.contains(x, y)
        }

        if (index >= 0) {
            selectedVariantIndex = index
            return
        }

        val nearestIndex =
            variantBounds.indices.minByOrNull { index ->
                val rect = variantBounds[index]

                val dx = when {
                    x < rect.left -> rect.left - x
                    x > rect.right -> x - rect.right
                    else -> 0f
                }

                val dy = when {
                    y < rect.top -> rect.top - y
                    y > rect.bottom -> y - rect.bottom
                    else -> 0f
                }

                dx * dx + dy * dy
            }

        if (nearestIndex != null) {
            selectedVariantIndex = nearestIndex
        }
    }

    private fun commitSelectedVariant() {
        val variant = longPressVariants.getOrNull(
            selectedVariantIndex
        ) ?: return

        if (variant.isNotEmpty()) {
            onKeyboardActionListener?.onText(variant)
        }
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

        deleteHandler.removeCallbacks(deleteRepeatRunnable)
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

            KeyAction.SEARCH -> {
                onKeyboardActionListener?.onSearch()
            }

            KeyAction.SWITCH_TO_ENGLISH -> {
                setKeyboardLayout(
                    if (
                        activeLanguage ==
                        KeyboardLayout.Language.ARABIC
                    ) {
                        KeyboardLayoutProvider.getArabicLayout()
                    } else {
                        KeyboardLayoutProvider.getEnglishLayout()
                    }
                )
            }

            KeyAction.SWITCH_TO_NUMBERS -> {
                setKeyboardLayout(
                    KeyboardLayoutProvider.getNumbersLayout(
                        activeLanguage
                    )
                )
            }

            KeyAction.SWITCH_TO_SYMBOLS -> {
                setKeyboardLayout(
                    KeyboardLayoutProvider.getSymbolsLayout(
                        activeLanguage
                    )
                )
            }

            KeyAction.SWITCH_LANGUAGE -> {
                activeLanguage =
                    if (
                        activeLanguage ==
                        KeyboardLayout.Language.ENGLISH
                    ) {
                        KeyboardLayout.Language.ARABIC
                    } else {
                        KeyboardLayout.Language.ENGLISH
                    }

                savePreferredLanguage(activeLanguage)

                setKeyboardLayout(
                    if (
                        activeLanguage ==
                        KeyboardLayout.Language.ARABIC
                    ) {
                        KeyboardLayoutProvider.getArabicLayout()
                    } else {
                        KeyboardLayoutProvider.getEnglishLayout()
                    }
                )
            }

            KeyAction.NONE -> {
                val output =
                    key.outputText
                        ?: getSymbolOutput(key)
                        ?: getDisplayLabel(key)

                if (output.isNotEmpty()) {
                    onKeyboardActionListener?.onText(output)

                    if (
                        shiftEnabled &&
                        key.code in
                        KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z
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

    private fun loadPreferredLanguage(): KeyboardLayout.Language {
        val savedLanguage = preferences.getString(
            "active_language",
            KeyboardLayout.Language.ENGLISH.name
        )

        return if (
            savedLanguage ==
            KeyboardLayout.Language.ARABIC.name
        ) {
            KeyboardLayout.Language.ARABIC
        } else {
            KeyboardLayout.Language.ENGLISH
        }
    }

    private fun savePreferredLanguage(
        language: KeyboardLayout.Language
    ) {
        preferences.edit()
            .putString("active_language", language.name)
            .apply()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun dp(value: Float): Float {
        return value * resources.displayMetrics.density
    }
}  
