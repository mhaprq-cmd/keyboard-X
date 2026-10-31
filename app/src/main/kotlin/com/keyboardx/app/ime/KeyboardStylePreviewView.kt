package com.keyboardx.app.ime

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Small preview of a keyboard style for the catalog cards.
 *
 * Uses the real keyboard layout and the same theme/style appearance
 * as the keyboard input view.
 */
class KeyboardStylePreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }

    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
    }

    private val clipPath = Path()
    private val keyRect = RectF()
    private val imageRect = RectF()

    private var backgroundBitmap: Bitmap? = null

    private val preferences =
        context.getSharedPreferences(
            "keyboard_x_preferences",
            Context.MODE_PRIVATE
        )

    fun setStyle(style: KeyboardStyle) {
        backgroundBitmap = style.backgroundRes?.let { resourceId ->
            decodeSampled(resourceId)
        }
        invalidate()
    }

    private fun decodeSampled(resourceId: Int): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inSampleSize = 4
        }

        return BitmapFactory.decodeResource(
            resources,
            resourceId,
            options
        )
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, (width * 0.58f).toInt())
    }

    private fun getPreviewLayout(): KeyboardLayout {
        val savedLanguages = preferences.getString(
            "enabled_languages",
            null
        )

        val enabledLanguages = savedLanguages
            ?.split(",")
            ?.mapNotNull { name ->
                KeyboardLayout.Language.values()
                    .firstOrNull { it.name == name.trim() }
            }
            ?.toSet()
            .orEmpty()
            .ifEmpty {
                setOf(
                    KeyboardLayout.Language.ARABIC,
                    KeyboardLayout.Language.ENGLISH
                )
            }

        val savedLanguage = preferences.getString(
            "active_language",
            KeyboardLayout.Language.ENGLISH.name
        )

        val preferredLanguage =
            if (
                savedLanguage ==
                KeyboardLayout.Language.ARABIC.name
            ) {
                KeyboardLayout.Language.ARABIC
            } else {
                KeyboardLayout.Language.ENGLISH
            }

        val selectedLanguage =
            if (preferredLanguage in enabledLanguages) {
                preferredLanguage
            } else {
                enabledLanguages.first()
            }

        return if (
            selectedLanguage == KeyboardLayout.Language.ARABIC
        ) {
            KeyboardLayoutProvider.getArabicLayout()
        } else {
            KeyboardLayoutProvider.getEnglishLayout()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width <= 0 || height <= 0) return

        val theme = KeyboardThemeManager.getCurrentTheme(context)
        val layout = getPreviewLayout()

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        clipPath.reset()
        clipPath.addRoundRect(
            0f,
            0f,
            viewWidth,
            viewHeight,
            dp(12f),
            dp(12f),
            Path.Direction.CW
        )

        canvas.save()
        canvas.clipPath(clipPath)

        fillPaint.color = theme.backgroundColor
        canvas.drawRect(
            0f,
            0f,
            viewWidth,
            viewHeight,
            fillPaint
        )

        backgroundBitmap?.let { bitmap ->
            imageRect.set(0f, 0f, viewWidth, viewHeight)
            canvas.drawBitmap(
                bitmap,
                null,
                imageRect,
                imagePaint
            )
        }

        val scale =
            viewWidth / resources.displayMetrics.widthPixels.toFloat()

        val padding = dp(4f) * scale
        val gap = dp(3f) * scale
        val inset = dp(theme.keyVisualInsetDp) * scale
        val radius = dp(theme.keyCornerRadiusDp) * scale

        strokePaint.strokeWidth =
            maxOf(1f, dp(theme.keyStrokeWidthDp) * scale)
        strokePaint.color = theme.keyStrokeColor
        fillPaint.color = theme.keyColor
        textPaint.color = theme.textColor

        val rows = layout.rows
        if (rows.isEmpty()) {
            canvas.restore()
            return
        }

        val rowHeight =
            (viewHeight - padding * 2f - gap * (rows.size - 1)) /
                rows.size

        var top = padding

        rows.forEach { row ->
            val keys = row.keys

            if (keys.isNotEmpty()) {
                val totalGap = gap * (keys.size - 1)

                val totalWeight = keys.sumOf { key ->
                    key.widthWeight.coerceAtLeast(0.1f).toDouble()
                }.toFloat()

                val availableWidth =
                    viewWidth - padding * 2f - totalGap

                var left = padding

                keys.forEach { key ->
                    val weight =
                        key.widthWeight.coerceAtLeast(0.1f)

                    val keyWidth =
                        availableWidth * weight / totalWeight

                    keyRect.set(
                        left + inset,
                        top + inset,
                        left + keyWidth - inset,
                        top + rowHeight - inset
                    )

                    canvas.drawRoundRect(
                        keyRect,
                        radius,
                        radius,
                        fillPaint
                    )

                    canvas.drawRoundRect(
                        keyRect,
                        radius,
                        radius,
                        strokePaint
                    )

                    val label = getPreviewLabel(key)

                    if (label.isNotEmpty()) {
                        textPaint.textSize = when {
                            label.length > 2 ->
                                dp(8f) * scale

                            else ->
                                dp(10f) * scale
                        }

                        val fontMetrics = textPaint.fontMetrics

                        val textCenterY =
                            keyRect.centerY() -
                                (fontMetrics.ascent +
                                    fontMetrics.descent) / 2f

                        canvas.drawText(
                            label,
                            keyRect.centerX(),
                            textCenterY,
                            textPaint
                        )
                    }

                    left += keyWidth + gap
                }
            }

            top += rowHeight + gap
        }

        canvas.restore()
    }

    private fun getPreviewLabel(key: Key): String {
        return when (key.action) {
            KeyAction.SPACE -> ""
            KeyAction.SHIFT -> "⇧"
            KeyAction.DELETE -> "⌫"
            KeyAction.ENTER -> "↵"
            KeyAction.SEARCH -> "⌕"
            KeyAction.SWITCH_LANGUAGE -> "🌐"
            else -> key.label
        }
    }

    private fun dp(value: Float): Float {
        return value * resources.displayMetrics.density
    }
}
