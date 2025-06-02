package com.example.game.entity

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.random.Random

class Star(var x: Float, var y: Float, private val radius: Float) {
    private val paint = Paint().apply { color = Color.WHITE }
    private val speed = 50f

    fun update(et: Float) {
        y += speed * (et / 1000f)
        if (y > 1800f) {
            y = -radius
            x = Random.nextFloat() * 1080f
        }
    }

    fun render(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, paint)
    }
}