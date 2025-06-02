package com.example.goto_the_moon.screen

import android.graphics.Color
import android.view.MotionEvent
import com.example.goto_the_moon.Game
import com.example.goto_the_moon.utils.ResourceLoader

class StartScreen(private val game: Game) : Screen(game) {
    private val resourceLoader = ResourceLoader(game.context)

    init {
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