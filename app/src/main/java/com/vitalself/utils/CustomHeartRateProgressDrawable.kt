package com.vitalself.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable

/**
 * A custom drawable for the heart rate progress bar that shows different
 * color regions for Low, Normal, and High heart rate ranges.
 */
class CustomHeartRateProgressDrawable(private val context: Context) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Colors for different sections
    private val lowRangeColors = intArrayOf(
        Color.parseColor("#FFEE58"),
        Color.parseColor("#FFE082")
    )

    private val normalRangeColors = intArrayOf(
        Color.parseColor("#66BB6A"),
        Color.parseColor("#81C784")
    )

    private val highRangeColors = intArrayOf(
        Color.parseColor("#EF5350"),
        Color.parseColor("#E57373")
    )

    // Ranges (percentages within the progress bar)
    private val lowRange = 0f..0.25f
    private val normalRange = 0.25f..0.75f
    private val highRange = 0.75f..1f

    // Current progress (0-100)
    var progress = 0
        set(value) {
            field = value.coerceIn(0, 100)
            invalidateSelf()
        }

    // Heart rate thresholds
    var minHeartRate = 40
    var normalLowHeartRate = 60
    var normalHighHeartRate = 100
    var maxHeartRate = 240

    // Current heart rate value
    var heartRate = 80
        set(value) {
            field = value
            // Calculate progress percentage based on heart rate value
            val range = maxHeartRate - minHeartRate
            val position = field - minHeartRate
            progress = ((position.toFloat() / range.toFloat()) * 100).toInt().coerceIn(0, 100)
        }

    init {
        // Background paint
        backgroundPaint.color = Color.parseColor("#E0E0E0")

        // Progress indicator paint
        indicatorPaint.color = Color.WHITE
        indicatorPaint.style = Paint.Style.FILL

        // Set initial heart rate
        heartRate = 80
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val height = bounds.height().toFloat()
        val width = bounds.width().toFloat()
        val cornerRadius = height / 2

        // Draw background
        val bgRect = RectF(bounds)
        canvas.drawRoundRect(bgRect, cornerRadius, cornerRadius, backgroundPaint)

        // Calculate ranges in pixels
        val lowRangeEnd = width * lowRange.endInclusive
        val normalRangeEnd = width * normalRange.endInclusive

        // Draw low range section
        val lowRangeRect = RectF(0f, 0f, lowRangeEnd, height)
        paint.shader = LinearGradient(
            0f, 0f, lowRangeEnd, 0f,
            lowRangeColors, null, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(lowRangeRect, cornerRadius, cornerRadius, paint)

        // Draw normal range section
        val normalRangeRect = RectF(lowRangeEnd, 0f, normalRangeEnd, height)
        paint.shader = LinearGradient(
            lowRangeEnd, 0f, normalRangeEnd, 0f,
            normalRangeColors, null, Shader.TileMode.CLAMP
        )
        canvas.drawRect(normalRangeRect, paint)

        // Draw high range section
        val highRangeRect = RectF(normalRangeEnd, 0f, width, height)
        paint.shader = LinearGradient(
            normalRangeEnd, 0f, width, 0f,
            highRangeColors, null, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(highRangeRect, cornerRadius, cornerRadius, paint)

        // Draw progress indicator (using progress value)
        val progressWidth = (progress / 100f) * width
        val indicatorRadius = height * 0.8f
        canvas.drawCircle(progressWidth, height / 2, indicatorRadius / 2, indicatorPaint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}