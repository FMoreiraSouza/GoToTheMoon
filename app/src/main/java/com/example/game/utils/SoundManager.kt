package com.example.game.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log

class SoundManager() {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .build()
        )
        .build()
    private val soundIds: MutableMap<String, Int> = mutableMapOf()
    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false

    fun loadSound(soundName: String, descriptor: AssetFileDescriptor) {
        try {
            val soundId = soundPool.load(descriptor, 1)
            soundIds[soundName] = soundId
        } catch (e: Exception) {
            Log.d("SoundManager", "Erro ao carregar som $soundName: ${e.message}")
        }
    }

    fun playSound(soundName: String, volume: Float = 1f) {
        soundIds[soundName]?.let {
            soundPool.play(it, volume, volume, 1, 0, 1f)
        } ?: Log.d("SoundManager", "Som $soundName não encontrado")
    }

    fun loadMusic(descriptor: AssetFileDescriptor) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(descriptor)
                isLooping = true
                prepare()
                isPrepared = true
            }
        } catch (e: Exception) {
            Log.d("SoundManager", "Erro ao carregar música: ${e.message}")
        }
    }

    fun playMusic() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                try {
                    if (!isPrepared) it.prepare()
                    it.start()
                } catch (e: Exception) {
                    Log.d("SoundManager", "Erro ao tocar música: ${e.message}")
                }
            }
        }
    }

    fun pauseMusic() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
    }

    fun release() {
        soundPool.release()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }
}