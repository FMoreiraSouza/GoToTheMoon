package com.example.goto_the_moon.entity

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class Ship(
    var x: Float,
    var y: Float,
    val radius: Float
) {
    private val paint = Paint().apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
    }
    private val shieldPaint = Paint().apply {
        color = android.graphics.Color.argb(150, 0, 0, 255)
        style = Paint.Style.FILL
    }

    fun render(canvas: Canvas, bitmap: Bitmap?, shieldActive: Boolean) {
        if (shieldActive) {
            canvas.drawCircle(x, y, 150f, shieldPaint)
        }
        bitmap?.let {
            val size = 250f
            val rect = RectF(x - size / 2, y - size / 2, x + size / 2, y + size / 2)
            canvas.drawBitmap(it, null, rect, null)
        } ?: canvas.drawCircle(x, y, radius, paint)
    }

    fun collidesWith(asteroid: Asteroid): Boolean {
        val dx = x - asteroid.x
        val dy = y - asteroid.y
        val distance = kotlin.math.sqrt(dx * dx + dy * dy)
        return distance < radius + asteroid.radius
    }
}