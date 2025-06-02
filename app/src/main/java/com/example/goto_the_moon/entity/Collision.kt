package com.example.goto_the_moon.entity

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Collision(val x: Float, val y: Float, val isLarge: Boolean = false) {
    private val particles = mutableListOf<Particle>()
    private val duration = if (isLarge) 1000f else 500f
    private var elapsedTime = 0f
    private val paint = Paint().apply { color = Color.argb(255, 255, 165, 0) }
    val isActive: Boolean get() = elapsedTime <= duration

    init {
        val particleCount = if (isLarge) 40 else 20
        repeat(particleCount) {
            val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
            val speed = Random.nextFloat() * (if (isLarge) 300f else 200f) + 100f
            val radius = if (isLarge) Random.nextFloat() * 15f + 5f else 10f
            particles.add(Particle(x, y, speed * cos(angle), speed * sin(angle), radius))
        }
    }

    fun update(et: Float): Boolean {
        elapsedTime += et
        if (!isActive) return false

        particles.forEach { it.update(et) }
        return true
    }

    fun render(canvas: Canvas) {
        particles.forEach { it.render(canvas, paint) }
    }

    private class Particle(
        var x: Float,
        var y: Float,
        private val vx: Float,
        private val vy: Float,
        private var radius: Float
    ) {
        fun update(et: Float) {
            x += vx * (et / 1000f)
            y += vy * (et / 1000f)
            radius *= 0.95f
        }

        fun render(canvas: Canvas, paint: Paint) {
            if (radius > 0) {
                canvas.drawCircle(x, y, radius, paint)
            }
        }
    }
}