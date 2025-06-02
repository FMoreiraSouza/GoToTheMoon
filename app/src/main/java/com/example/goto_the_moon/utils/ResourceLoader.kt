package com.example.goto_the_moon.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.scale

class ResourceLoader(private val context: Context) {
    private val bitmaps: MutableMap<String, Bitmap> = mutableMapOf()

    fun loadBitmap(assetName: String, width: Int, height: Int): Bitmap? {
        return try {
            val tempBitmap = BitmapFactory.decodeStream(context.assets.open(assetName))
            val scaledBitmap = tempBitmap.scale(width, height)
            tempBitmap.recycle()
            bitmaps[assetName] = scaledBitmap
            scaledBitmap
        } catch (e: Exception) {
            Log.d("ResourceLoader", "Erro ao carregar bitmap $assetName: ${e.message}")
            null
        }
    }

    fun getBitmap(assetName: String): Bitmap? = bitmaps[assetName]

    fun release() {
        bitmaps.values.forEach { it.recycle() }
        bitmaps.clear()
    }
}