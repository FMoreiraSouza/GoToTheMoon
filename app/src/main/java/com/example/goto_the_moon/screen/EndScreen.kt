package com.example.goto_the_moon.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import com.example.goto_the_moon.Game

class EndScreen(private val game: Game, private val isWin: Boolean) : Screen(game) {
    private var textY = 0f
    private var retryButtonX = 0f
    private var retryButtonY = 0f
    private var retryButtonWidth = 0f
    private val retryButtonHeight = 100f
    private var exitButtonX = 0f
    private var exitButtonY = 0f
    private var exitButtonWidth = 0f
    private val exitButtonHeight = 100f
    private val padding = 36f
    private val cornerRadius = 48f
    private var backgroundBitmap: Bitmap? = null

    init {
        paint.textAlign = Paint.Align.CENTER
        paint.isFakeBoldText = true
        try {
            paint.typeface = Typeface.createFromAsset(game.context.assets, "Chalkboard.ttf")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        paint.textSize = 48f
        retryButtonWidth = paint.measureText("Tentar novamente") + 2 * padding
        exitButtonWidth = paint.measureText("Sair") + 2 * padding

        retryButtonX = (canvas.width - retryButtonWidth) / 2
        exitButtonX = (canvas.width - exitButtonWidth) / 2

        paint.textSize = 72f
        textY = 150f
        retryButtonY = textY + 100f
        exitButtonY = retryButtonY + retryButtonHeight + 30f

        try {
            val assetPath = if (isWin) "end_win.png" else "end_loss.png"
            game.context.assets.open(assetPath).use { inputStream ->
                backgroundBitmap = BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun update(et: Float) = Unit

    @SuppressLint("UseKtx")
    override fun draw() {
        backgroundBitmap?.let {
            val destRect = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
            canvas.drawBitmap(it, null, destRect, null)
        } ?: run {
            paint.color = Color.parseColor("#FAFAFA")
            canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)
        }

        paint.color = Color.parseColor(if (isWin) "#AED581" else "#FF0000")
        paint.textSize = 72f
        val message = if (isWin) "Parabéns! Você ganhou!" else "Você perdeu!"
        canvas.drawText(message, canvas.width / 2f, textY, paint)

        paint.color = Color.parseColor("#2196F3")
        canvas.drawRoundRect(
            retryButtonX, retryButtonY, retryButtonX + retryButtonWidth, retryButtonY + retryButtonHeight,
            cornerRadius, cornerRadius, paint
        )
        paint.color = Color.parseColor("#FFFFFF")
        paint.textSize = 48f
        canvas.drawText(
            "Tentar novamente",
            retryButtonX + retryButtonWidth / 2,
            retryButtonY + retryButtonHeight / 2 + 16f,
            paint
        )

        paint.color = Color.parseColor("#2196F3")
        canvas.drawRoundRect(
            exitButtonX, exitButtonY, exitButtonX + exitButtonWidth, exitButtonY + exitButtonHeight,
            cornerRadius, cornerRadius, paint
        )
        paint.color = Color.parseColor("#FFFFFF")
        paint.textSize = 48f
        canvas.drawText(
            "Sair",
            exitButtonX + exitButtonWidth / 2,
            exitButtonY + exitButtonHeight / 2 + 16f,
            paint
        )
    }

    override fun handleEvent(event: Int, x: Float, y: Float) {
        if (event == MotionEvent.ACTION_DOWN) {
            if (x >= retryButtonX && x <= retryButtonX + retryButtonWidth &&
                y >= retryButtonY && y <= retryButtonY + retryButtonHeight
            ) {
                game.actualScreen = SpaceScreen(game)
            }
            if (x >= exitButtonX && x <= exitButtonX + exitButtonWidth &&
                y >= exitButtonY && y <= exitButtonY + exitButtonHeight
            ) {
                game.actualScreen = StartScreen(game)
            }
        }
    }

    override fun onPause() {
        backgroundBitmap?.recycle()
        backgroundBitmap = null
    }

    override fun onResume() {
        try {
            val assetPath = if (isWin) "end_win.png" else "end_loss.png"
            game.context.assets.open(assetPath).use { inputStream ->
                backgroundBitmap = BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun backPressed() {
        game.actualScreen = StartScreen(game)
    }
}