package com.example.game.entity

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import androidx.core.graphics.scale

class Asteroid(
    var x: Float,
    var y: Float,
    var speed: Float,
    var health: Int = 2,
    var isGiant: Boolean = false,
    var isCurved: Boolean = false,
    private var targetX: Float? = null
) {
    val radius: Float = if (isGiant) 100f else 50f
    private val rect = RectF(x - radius, y - radius, x + radius, y + radius)
    private val curveSpeed: Float = if (isGiant) 50f else 100f

    fun update(et: Float, shipX: Float? = null) {
        y += speed * (et / 1000f)

        if (isCurved && targetX != null && shipX != null) {
            val dx = shipX - x
            x += (dx * curveSpeed * (et / 1000f)).coerceIn(
                -curveSpeed * (et / 1000f),
                curveSpeed * (et / 1000f)
            )
        }

        rect.set(x - radius, y - radius, x + radius, y + radius)
    }

    fun render(canvas: Canvas, bitmap: Bitmap?) {
        bitmap?.let {
            if (isGiant) {
                val scaledBitmap =
                    it.scale((200 * 2), (200 * 2))
                canvas.drawBitmap(
                    scaledBitmap,
                    x - radius * 2,
                    y - radius * 2,
                    null
                )
                scaledBitmap.recycle()
            } else {
                canvas.drawBitmap(it, x - radius, y - radius, null)
            }
        }
    }
}