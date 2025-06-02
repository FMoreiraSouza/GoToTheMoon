package com.example.goto_the_moon.entity

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log

class Laser(var x: Float, var y: Float) {
    var speed = -400f
    private val radius = 10f
    private val paint = Paint().apply { color = Color.YELLOW }

    fun update(dt: Float) {
        y += speed * (dt / 1000f)
    }

    fun render(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, paint)
    }

    fun collidesWith(asteroid: Asteroid): Boolean {
        val dx = x - asteroid.x
        val dy = y - asteroid.y
        val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        val collision = distance < (radius + asteroid.radius)
        Log.d("CollisionDebug", "Laser(x=$x, y=$y, radius=$radius), Asteroid(x=${asteroid.x}, y=${asteroid.y}, radius=${asteroid.radius}), Distance=$distance, Collision=$collision")
        return collision
    }
}