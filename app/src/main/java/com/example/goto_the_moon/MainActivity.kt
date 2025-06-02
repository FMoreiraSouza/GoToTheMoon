package com.example.goto_the_moon

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.goto_the_moon.screen.StartScreen
import com.example.goto_the_moon.utils.Fonts

class MainActivity : AppCompatActivity() {
    private var game: Game? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Fonts.initializeFonts(this)
        game = Game(this).also { g ->
            setContentView(g.render)
        }
        game?.let { g ->
            g.actualScreen = StartScreen(g)
        }
    }

    override fun onPause() {
        super.onPause()
        game?.onPause()
    }

    override fun onResume() {
        super.onResume()
        game?.onResume()
    }
}