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
 * It uses the same sources as the real keyboard:
 * - colors, corner radius and stroke from KeyboardThemeManager
 *   (so it follows the system light/dark mode),
 * - the style background drawn behind the keys, then the keys on top.
 *
 * The keys are drawn without letters. Only their appearance is shown.
 */
class KeyboardStylePreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rowWeights: List<List<Float>> = listOf(
        List(10) { 1f },
        List(9) { 1f },
        List(9) { 1f },
        listOf(1f, 1f, 4f, 1f, 1f)
    )

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
    }

    private val clipPath = Path()
    private val keyRect = RectF()
    private val imageRect = RectF()

    private var backgroundBitmap: Bitmap? = null

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
        setMeasuredDimension(width, (width * 0.5f).toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width <= 0 || height <= 0) return

        val theme = KeyboardThemeManager.getCurrentTheme(context)

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
        canvas.drawRect(0f, 0f, viewWidth, viewHeight, fillPaint)

        backgroundBitmap?.let { bitmap ->
            imageRect.set(0f, 0f, viewWidth, viewHeight)
            canvas.drawBitmap(bitmap, null, imageRect, imagePaint)
        }

        // Same proportions as the real keyboard, scaled to the preview width.
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

        val rowCount = rowWeights.size

        val rowHeight =
            (viewHeight - padding * 2f - gap * (rowCount - 1)) / rowCount

        var top = padding

        rowWeights.forEach { row ->
            val totalGap = gap * (row.size - 1)
            val totalWeight = row.sum()

            val availableWidth =
                viewWidth - padding * 2f - totalGap

            var left = padding

            row.forEach { weight ->
                val keyWidth = availableWidth * weight / totalWeight

                keyRect.set(
                    left + inset,
                    top + inset,
                    left + keyWidth - inset,
                    top + rowHeight - inset
                )

                canvas.drawRoundRect(keyRect, radius, radius, fillPaint)
                canvas.drawRoundRect(keyRect, radius, radius, strokePaint)

                left += keyWidth + gap
            }

            top += rowHeight + gap
        }

        canvas.restore()
    }

    private fun dp(value: Float): Float {
        return value * resources.displayMetrics.density
    }
}
