package com.example.game

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.graphics.createBitmap
import com.example.game.screen.Screen
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class Game(val context: Context) {

    var buffer: Bitmap
    var render: Render
    var actualScreen: Screen? = null

    private var nWid = 0f
    private var nHei = 0f;
    private var hDist = 0f
    private var vDist = 0f
    private var sx = 0f
    private var sy = 0f

    init {
        val isLandscape =
            context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val bufferWidth = if (isLandscape) 1920f else 1080f // Aumentado para maior qualidade
        val bufferHeight = if (isLandscape) 1080f else 1920f
        buffer = createBitmap(bufferWidth.toInt(), bufferHeight.toInt())

        val screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = context.resources.displayMetrics.heightPixels.toFloat()

        val aspectBuffer = bufferWidth / bufferHeight
        val aspectScreen = screenWidth / screenHeight

        if (aspectBuffer > aspectScreen || abs(aspectScreen - aspectBuffer) < 0.1) {
            this.nWid = screenWidth
            val fAsp = aspectScreen / aspectBuffer
            this.nHei = screenHeight * fAsp
            this.vDist = (screenHeight - nHei) / 2
        } else {
            this.nHei = screenHeight
            this.nWid = nHei * aspectBuffer
            this.hDist = (screenWidth - nWid) / 2
        }

        sx = nWid / bufferWidth
        sy = nHei / bufferHeight

        render = Render(context, buffer)
        render.setOnTouchListener(render)
    }

    inner class Render(context: Context, private val buffer: Bitmap) : View(context),
        OnTouchListener {

        private var startTime = System.nanoTime()
        private var paint = Paint()
        private var src: Rect = Rect()
        private var dst: Rect = Rect()

        init {
            paint.isAntiAlias = true
            paint.isFilterBitmap = true
            paint.isDither = true
            src.set(0, 0, buffer.width, buffer.height)
            dst.set(hDist.toInt(), vDist.toInt(), (nWid + hDist).toInt(), (nHei + vDist).toInt())
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val elapsedTime = (System.nanoTime() - startTime) / 1000000f
            startTime = System.nanoTime()

            actualScreen?.update(elapsedTime)
            actualScreen?.draw()

            canvas.also { c ->
                c.drawColor(Color.BLACK)
                c.drawBitmap(buffer, src, dst, paint)
            }

            invalidate()
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {

            event?.let { e ->
                val x = (e.x - hDist) / sx
                val y = (e.y - vDist) / sy
                actualScreen?.handleEvent(e.action, x, y)
            }

            return true
        }
    }

    fun onPause() {
        actualScreen?.onPause()
    }

    fun onResume() {
        actualScreen?.onResume()
    }

    fun backPressed() {
        actualScreen?.backPressed()
    }
}