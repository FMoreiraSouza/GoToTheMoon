package com.example.game.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import com.example.game.entity.Asteroid
import com.example.game.entity.Collision
import com.example.game.entity.Laser
import com.example.game.entity.PowerUp
import com.example.game.entity.Ship
import com.example.game.entity.Star
import com.example.game.enum.PowerUpType
import kotlin.random.Random

class EntityManager(
    private val soundManager: SoundManager,
    private val onScoreUpdate: (Int) -> Unit,
    private val onLivesUpdate: (Int) -> Unit,
    private val onGameOver: () -> Unit,
    private val onShake: () -> Unit
) {
    private val asteroids: MutableList<Asteroid> = mutableListOf()
    private val lasers: MutableList<Laser> = mutableListOf()
    private val powerUps: MutableList<PowerUp> = mutableListOf()
    val collisions: MutableList<Collision> = mutableListOf()
    private val stars: List<Star> = List(50) {
        Star(Random.nextFloat() * 1080f, Random.nextFloat() * 1800f, Random.nextFloat() * 2f + 1f)
    }
    private var lastSpawnTime: Float = 0f
    private val spawnInterval: Float = 600f
    private var combo: Int = 0
    private var comboTimer: Float = 0f
    private val comboResetTime: Float = 2000f
    private var score: Int = 0
    private var lives: Int = 5
    private var doubleShotActive: Boolean = false
    private var doubleShotTimer: Float = 0f
    var shieldActive: Boolean = false
    private var shieldTimer: Float = 0f

    fun update(
        et: Float,
        ship: Ship,
        shipX: Float,
        onFlash: () -> Unit,
        isGameOver: Boolean
    ) {
        if (isGameOver) return

        stars.forEach { it.update(et) }
        asteroids.forEach { it.update(et, shipX) }
        lasers.forEach { it.update(et) }
        powerUps.forEach { it.update(et) }
        collisions.forEach { it.update(et) }

        asteroids.removeAll { it.y > 1800f }
        lasers.removeAll { it.y < 0 }
        powerUps.removeAll { it.y > 1800f }
        collisions.removeAll { !it.isActive }

        spawnAsteroids(et, shipX)
        spawnPowerUps()

        handleCollisions(ship, onFlash)

        updateTimers(et)
    }

    private fun spawnAsteroids(et: Float, shipX: Float) {
        lastSpawnTime += et
        if (lastSpawnTime >= spawnInterval) {
            if (Random.nextFloat() < 0.1f) {
                val isCurved = Random.nextFloat() < 0.2f
                asteroids.add(
                    Asteroid(
                        x = Random.nextFloat() * 1080f,
                        y = -50f,
                        speed = Random.nextFloat() * 100f + 200f,
                        health = 15,
                        isGiant = true,
                        isCurved = isCurved,
                        targetX = if (isCurved) shipX else null
                    )
                )
            } else {
                repeat(Random.nextInt(1, 3)) {
                    val isCurved = Random.nextFloat() < 0.2f
                    asteroids.add(
                        Asteroid(
                            x = Random.nextFloat() * 1080f,
                            y = -50f,
                            speed = Random.nextFloat() * 150f + 250f,
                            health = 2,
                            isGiant = false,
                            isCurved = isCurved,
                            targetX = if (isCurved) shipX else null
                        )
                    )
                }
            }
            lastSpawnTime = 0f
        }
    }

    private fun spawnPowerUps() {
        if (Random.nextFloat() < 0.005f) {
            val type = PowerUpType.entries.toTypedArray().random()
            powerUps.add(PowerUp(Random.nextFloat() * 1080f, -50f, type))
        }
    }

    private fun handleCollisions(ship: Ship, onFlash: () -> Unit) {
        val asteroidIterator = asteroids.iterator()
        while (asteroidIterator.hasNext()) {
            val asteroid = asteroidIterator.next()
            if (ship.collidesWith(asteroid)) {
                if (!shieldActive) {
                    lives -= if (asteroid.isGiant) 2 else 1
                    onLivesUpdate(lives)
                    if (lives > 0) {
                        collisions.add(Collision(ship.x, ship.y))
                        onShake()
                        onFlash()
                        soundManager.playSound("collision")
                    } else {
                        collisions.add(Collision(ship.x, ship.y, isLarge = true))
                        onGameOver()
                    }
                    asteroidIterator.remove()
                }
            }
        }

        val laserIterator = lasers.iterator()
        while (laserIterator.hasNext()) {
            val laser = laserIterator.next()
            val asteroidIterator2 = asteroids.iterator()
            while (asteroidIterator2.hasNext()) {
                val asteroid = asteroidIterator2.next()
                if (laser.collidesWith(asteroid)) {
                    asteroid.health--
                    laserIterator.remove()
                    if (asteroid.health <= 0) {
                        asteroidIterator2.remove()
                        combo++
                        comboTimer = comboResetTime
                        score += 10 * combo * (if (asteroid.isGiant) 5 else 1)
                        onScoreUpdate(score)
                        collisions.add(Collision(asteroid.x, asteroid.y, isLarge = asteroid.isGiant))
                        soundManager.playSound("collision")
                    } else {
                        collisions.add(Collision(asteroid.x, asteroid.y))
                        soundManager.playSound("collision", 0.5f)
                    }
                    break
                }
            }
        }

        val powerUpIterator = powerUps.iterator()
        while (powerUpIterator.hasNext()) {
            val powerUp = powerUpIterator.next()
            if (powerUp.collidesWith(ship)) {
                when (powerUp.type) {
                    PowerUpType.SHIELD -> {
                        shieldActive = true
                        shieldTimer = 5000f
                    }
                    PowerUpType.EXTRA_LIFE -> {
                        lives = (lives + 1).coerceAtMost(5)
                        onLivesUpdate(lives)
                    }
                    PowerUpType.DOUBLE_SHOT -> {
                        doubleShotActive = true
                        doubleShotTimer = 5000f
                    }
                }
                powerUpIterator.remove()
            }
        }
    }

    private fun updateTimers(et: Float) {
        if (doubleShotActive) {
            doubleShotTimer -= et
            if (doubleShotTimer <= 0) doubleShotActive = false
        }
        if (shieldActive) {
            shieldTimer -= et
            if (shieldTimer <= 0) shieldActive = false
        }
        if (comboTimer > 0) {
            comboTimer -= et
            if (comboTimer <= 0) combo = 0
        }
    }

    fun render(canvas: Canvas, asteroidBitmap: Bitmap?) {
        stars.forEach { it.render(canvas) }
        asteroids.forEach { it.render(canvas, asteroidBitmap) }
        lasers.forEach { it.render(canvas) }
        powerUps.forEach { it.render(canvas) }
        collisions.forEach { it.render(canvas) }
    }

    fun addLaser(x: Float, y: Float) {
        if (doubleShotActive) {
            val offset = 20f
            lasers.add(Laser(x - offset, y))
            lasers.add(Laser(x + offset, y))
        } else {
            lasers.add(Laser(x, y))
        }
        soundManager.playSound("laser")
    }
}