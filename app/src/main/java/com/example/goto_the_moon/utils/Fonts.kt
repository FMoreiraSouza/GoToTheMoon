package com.example.goto_the_moon.utils

import android.content.Context
import android.graphics.Typeface
import android.util.Log

object Fonts {
    lateinit var chalkboard: Typeface

    fun initializeFonts(context: Context) {
        try {
            chalkboard = Typeface.createFromAsset(context.assets, "chalkboard.ttf")
        } catch (e: Exception) {
            Log.d("Framework!", "Fonte não encontrada!")
        }
    }
}