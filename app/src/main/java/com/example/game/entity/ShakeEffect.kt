package com.example.game.entity

import android.graphics.Canvas
import kotlin.random.Random

class ShakeEffect {
    private var elapsedTime = 0f
    private val duration = 300f
    private var offsetX = 0f
    private var offsetY = 0f

    fun update(et: Float): Boolean {
        elapsedTime += et
        if (elapsedTime > duration) return false

        offsetX = Random.nextFloat() * 20f - 10f
        offsetY = Random.nextFloat() * 20f - 10f
        return true
    }

    fun apply(canvas: Canvas) {
        canvas.translate(offsetX, offsetY)
    }
}