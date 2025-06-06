package com.vitalself.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CircularProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var progress = 0f // Progress value (0 to 10)
    private var maxProgress = 10f
    private val startAngle = -90f // Start at top
    private val strokeWidth = 40f

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = this@CircularProgressBar.strokeWidth
    }

    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val gradientColors = intArrayOf(
        Color.parseColor("#FFA500"), // Orange
        Color.parseColor("#FFD700"), // Yellow
        Color.parseColor("#008000")  // Green
    )

    private lateinit var gradient: SweepGradient

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null) // For shadow and smooth blending
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val radius = width / 2f - strokeWidth
        gradient = SweepGradient(width / 2f, height / 2f, gradientColors, null)
        val matrix = Matrix()
        matrix.preRotate(startAngle, width / 2f, height / 2f)
        gradient.setLocalMatrix(matrix)
        progressPaint.shader = gradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val radius = width / 2f - strokeWidth
        val rect = RectF(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth)

        // Draw the circular progress
        canvas.drawArc(rect, startAngle, (progress / maxProgress) * 360, false, progressPaint)

        // Calculate the position of the indicator dot
        val angle = Math.toRadians((startAngle + (progress / maxProgress) * 360).toDouble())
        val cx = (width / 2 + radius * Math.cos(angle)).toFloat()
        val cy = (height / 2 + radius * Math.sin(angle)).toFloat()

        // Set the indicator color based on the progress
        indicatorPaint.color = when (progress.toInt()) {
            in 1..4 -> Color.parseColor("#FFA500") // Orange
            in 5..7 -> Color.parseColor("#FFD700") // Yellow
            else -> Color.parseColor("#008000") // Green
        }

        // Draw the indicator dot
        canvas.drawCircle(cx, cy, strokeWidth / 2, indicatorPaint)
    }

    fun setProgress(value: Float) {
        progress = value.coerceIn(0f, maxProgress)
        invalidate() // Redraw the view
    }
}
