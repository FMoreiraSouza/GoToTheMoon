package com.example.goto_the_moon.entity

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.goto_the_moon.enum.PowerUpType


class PowerUp(var x: Float, var y: Float, val type: PowerUpType) {
    private val radius = 20f
    private val speed = 100f
    private val paint = Paint().apply {
        color = when (type) {
            PowerUpType.DOUBLE_SHOT -> Color.YELLOW
            PowerUpType.SHIELD -> Color.BLUE
            PowerUpType.EXTRA_LIFE -> Color.GREEN
        }
    }

    fun update(dt: Float) {
        y += speed * (dt / 1000f)
    }

    fun render(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, paint)
    }

    fun collidesWith(ship: Ship): Boolean {
        val dx = x - ship.x
        val dy = y - ship.y
        val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        return distance < (radius + ship.radius)
    }
}