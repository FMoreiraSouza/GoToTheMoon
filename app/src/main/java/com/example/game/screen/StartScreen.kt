package com.example.game.screen

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import com.example.game.Game
import com.example.game.utils.ResourceLoader
import androidx.core.graphics.toColorInt

class StartScreen(private val game: Game) : Screen(game) {
    private val resourceLoader = ResourceLoader(game.context)
    private val titlePaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        textSize = 40f
        color = Color.WHITE
        setShadowLayer(6f, 1f, 1f, Color.BLACK)
    }

    private val startPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        textSize = 60f
        color = "#FFFF00".toColorInt()
        setShadowLayer(6f, 1f, 1f, Color.BLACK)
    }

    init {
        try {
            titlePaint.typeface = Typeface.createFromAsset(game.context.assets, "Chalkboard.ttf")
            startPaint.typeface = Typeface.createFromAsset(game.context.assets, "Chalkboard.ttf")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        loadResources()
    }

    private fun loadResources() {
        resourceLoader.loadBitmap("start.png", 1080, 1920)
    }

    override fun update(dt: Float) {
    }

    override fun draw() {
        resourceLoader.getBitmap("start.png")?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        } ?: canvas.drawColor(Color.BLACK)

        val titleX = canvas.width / 2f
        val titleY = 140f
        canvas.drawText("Go to the Moon", titleX, titleY, titlePaint)

        val startX = canvas.width / 2f
        val startY = 440f
        canvas.drawText("Iniciar", startX, startY, startPaint)
    }

    override fun handleEvent(event: Int, x: Float, y: Float) {
        if (event == MotionEvent.ACTION_DOWN) {
            game.actualScreen = SpaceScreen(game)
        }
    }

    override fun onPause() {
        resourceLoader.release()
    }

    override fun onResume() {
        loadResources()
    }

    override fun backPressed() {
    }
}