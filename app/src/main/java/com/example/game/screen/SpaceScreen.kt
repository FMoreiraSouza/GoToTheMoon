package com.example.game.screen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.example.game.Game
import com.example.game.R
import com.example.game.entity.ShakeEffect
import com.example.game.entity.Ship
import com.example.game.utils.EntityManager
import com.example.game.utils.ResourceLoader
import com.example.game.utils.SoundManager
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.core.graphics.toColorInt
import androidx.core.graphics.createBitmap

class SpaceScreen(private val game: Game) : Screen(game) {
    private val ship = Ship(540f, 1500f, 50f)
    private val resourceLoader = ResourceLoader(game.context)
    private val soundManager = SoundManager()
    private val entityManager = EntityManager(
        soundManager = soundManager,
        onScoreUpdate = { score = it },
        onLivesUpdate = { lives = it },
        onGameOver = { pendingGameOver = true },
        onShake = { shakeEffect = ShakeEffect() }
    )
    private var backgroundY: Float = 0f
    private var score: Int = 0
    private var lives: Int = 5
    private var flashAlpha: Int = 0
    private var flashTimer: Float = 0f
    private var gameOverAlpha: Int = 0
    private var gameTimer: Float = 0f
    private val winTime: Float = 60000f
    private var gameOver: Boolean = false
    private var pendingGameOver: Boolean = false
    private var gameEnded: Boolean = false
    private var shakeEffect: ShakeEffect? = null
    private val backgroundSpeed = 100f
    private val buttonPadding = 20f
    private val fireButtonRect = RectF(
        880f - buttonPadding, 1600f - buttonPadding,
        1060f - buttonPadding, 1780f - buttonPadding
    )
    private val fireButtonPaint = Paint().apply {
        color = Color.argb(80, 255, 0, 0)
        style = Paint.Style.FILL
    }
    private val fireButtonPressedPaint = Paint().apply {
        color = Color.argb(150, 255, 0, 0)
        style = Paint.Style.FILL
    }
    private val iconPaint = Paint().apply {
        isAntiAlias = true
        colorFilter = PorterDuffColorFilter("#FFFF00".toColorInt(), PorterDuff.Mode.SRC_IN)
    }
    private var fireIcon: Bitmap? = null
    private val lifeBarPaint = Paint().apply {
        color = "#00FF00".toColorInt()
        style = Paint.Style.FILL
    }
    private val lifeBarBorderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val lifeBarEmptyPaint = Paint().apply {
        color = "#333333".toColorInt()
        style = Paint.Style.FILL
    }
    private val timerTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 60f
        textAlign = Paint.Align.CENTER
        setShadowLayer(4f, 2f, 2f, Color.BLACK)
    }
    private val timerBackgroundPaint = Paint().apply {
        color = Color.argb(150, 0, 0, 0)
        style = Paint.Style.FILL
    }
    private val timerProgressPaint = Paint().apply {
        color = "#2196F3".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }
    private var targetX: Float? = null
    private var targetY: Float? = null
    private val shipSpeed: Float = 500f
    private var isFiring: Boolean = false
    private var fireCooldown: Float = 0f
    private val fireInterval: Float = 200f

    init {
        paint.color = Color.BLUE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 50f
        try {
            timerTextPaint.typeface = Typeface.createFromAsset(game.context.assets, "Chalkboard.ttf")
            paint.typeface = Typeface.createFromAsset(game.context.assets, "Chalkboard.ttf")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        loadResources()
    }

    private fun loadResources() {
        resourceLoader.loadBitmap("ship.png", 250, 250)
        resourceLoader.loadBitmap("asteroid.png", 200, 200)
        resourceLoader.loadBitmap("space_background.png", 1080, 1920)
        game.context.assets.openFd("collision.mp3").use { soundManager.loadSound("collision", it) }
        game.context.assets.openFd("game_over.mp3").use { soundManager.loadSound("game_over", it) }
        game.context.assets.openFd("laser.mp3").use { soundManager.loadSound("laser", it) }
        game.context.assets.openFd("mission_complete.mp3").use { soundManager.loadSound("mission_complete", it) }
        val drawable = ContextCompat.getDrawable(game.context, R.drawable.ic_fire_laser)
        fireIcon = drawable?.let {
            val bitmap = createBitmap(100, 100)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
            bitmap
        }
    }

    override fun update(et: Float) {
        if (gameOver || gameEnded) return

        gameTimer += et
        if (gameTimer >= winTime && lives > 0) {
            gameEnded = true
            soundManager.playSound("mission_complete")
            game.actualScreen = EndScreen(game, isWin = true)
            return
        }

        if (isFiring) {
            fireCooldown -= et
            if (fireCooldown <= 0f) {
                entityManager.addLaser(ship.x, ship.y - 50f)
                fireCooldown = fireInterval
            }
        }

        targetX?.let { tx ->
            targetY?.let { ty ->
                val dx = tx - ship.x
                val dy = ty - ship.y
                val distance = sqrt(dx * dx + dy * dy)
                if (distance > 5f) {
                    val speed = shipSpeed * (et / 1000f)
                    val moveX = (dx / distance) * speed
                    val moveY = (dy / distance) * speed
                    ship.x = (ship.x + moveX).coerceIn(50f, 1030f)
                    ship.y = (ship.y + moveY).coerceIn(100f, 1800f)
                } else {
                    ship.x = tx.coerceIn(50f, 1030f)
                    ship.y = ty.coerceIn(100f, 1800f)
                }
            }
        }

        entityManager.update(
            et = et,
            ship = ship,
            shipX = ship.x,
            onFlash = { flashAlpha = 100; flashTimer = 100f },
            isGameOver = gameOver,
        )

        backgroundY += backgroundSpeed * (et / 1000f)
        if (backgroundY >= 1920f) backgroundY -= 1920f

        if (flashTimer > 0) {
            flashTimer -= et
            if (flashTimer <= 0) flashAlpha = 0
        }
        if (gameOver && gameOverAlpha < 255) {
            gameOverAlpha = (gameOverAlpha + (et / 10f).toInt()).coerceAtMost(255)
        }

        shakeEffect?.let {
            if (!it.update(et)) shakeEffect = null
        }

        if (pendingGameOver && entityManager.collisions.isEmpty()) {
            gameOver = true
            gameEnded = true
            soundManager.playSound("game_over")
            game.actualScreen = EndScreen(game, isWin = false)
        }
    }

    override fun draw() {
        resourceLoader.getBitmap("space_background.png")?.let {
            canvas.drawBitmap(it, 0f, backgroundY, null)
            canvas.drawBitmap(it, 0f, backgroundY - 1920f, null)
        } ?: canvas.drawColor(Color.BLACK)

        shakeEffect?.apply(canvas)
        entityManager.render(canvas, resourceLoader.getBitmap("asteroid.png"))
        ship.render(canvas, resourceLoader.getBitmap("ship.png"), entityManager.shieldActive)

        if (flashAlpha > 0) {
            canvas.drawColor(Color.argb(flashAlpha, 255, 255, 255))
        }

        val centerX = fireButtonRect.centerX()
        val centerY = fireButtonRect.centerY()
        val radius = fireButtonRect.width() / 2
        canvas.drawCircle(centerX, centerY, radius, if (isFiring) fireButtonPressedPaint else fireButtonPaint)

        fireIcon?.let {
            val iconSize = radius * (if (isFiring) 1.08f else 1.2f)
            val iconRect = RectF(
                centerX - iconSize / 2,
                centerY - iconSize / 2,
                centerX + iconSize / 2,
                centerY + iconSize / 2
            )
            canvas.drawBitmap(it, null, iconRect, iconPaint)
        } ?: run {
            val fallbackPaint = Paint().apply {
                color = "#FFFF00".toColorInt()
                style = Paint.Style.FILL
            }
            val fallbackRadius = radius * (if (isFiring) 0.54f else 0.6f)
            canvas.drawCircle(centerX, centerY, fallbackRadius, fallbackPaint)
        }

        val barWidth = 40f
        val barHeight = 80f
        val barSpacing = 10f
        val totalWidth = 5 * barWidth + 4 * barSpacing
        val startX = (canvas.width - totalWidth) / 2
        val startY = 50f

        for (i in 0 until 5) {
            val left = startX + i * (barWidth + barSpacing)
            val rect = RectF(left, startY, left + barWidth, startY + barHeight)
            if (i < lives) {
                canvas.drawRect(rect, lifeBarPaint)
            } else {
                canvas.drawRect(rect, lifeBarEmptyPaint)
            }
            canvas.drawRect(rect, lifeBarBorderPaint)
        }

        val elapsedTime = (gameTimer / 1000f).coerceAtMost(60f).toInt() + 1
        val timerX = canvas.width / 2f
        val timerY = startY + barHeight + 90f
        val timerRadius = 50f
        canvas.drawCircle(timerX, timerY, timerRadius + 10f, timerBackgroundPaint)
        val progressAngle = (elapsedTime / 60f) * 360f
        for (angle in 0..progressAngle.toInt() step 5) {
            val rad = Math.toRadians(angle.toDouble())
            val startXProgress = timerX + (timerRadius + 5f) * cos(rad).toFloat()
            val startYProgress = timerY + (timerRadius + 5f) * sin(rad).toFloat()
            val endXProgress = timerX + (timerRadius + 15f) * cos(rad).toFloat()
            val endYProgress = timerY + (timerRadius + 15f) * sin(rad).toFloat()
            canvas.drawLine(startXProgress, startYProgress, endXProgress, endYProgress, timerProgressPaint)
        }
        canvas.drawText("$elapsedTime", timerX, timerY + 20f, timerTextPaint)

        if (gameOver) {
            paint.color = Color.argb(gameOverAlpha, 255, 0, 0)
            paint.textSize = 100f
            canvas.drawText("Game Over", 540f, 900f, paint)
            paint.color = Color.argb(gameOverAlpha, 255, 255, 255)
            paint.textSize = 50f
            canvas.drawText("Toque para voltar", 540f, 1000f, paint)
        }
    }

    override fun handleEvent(event: Int, x: Float, y: Float) {
        if (gameOver && event == MotionEvent.ACTION_DOWN) {
            game.actualScreen = StartScreen(game)
            return
        }
        if (event == MotionEvent.ACTION_DOWN || event == MotionEvent.ACTION_MOVE) {
            val centerX = fireButtonRect.centerX()
            val centerY = fireButtonRect.centerY()
            val radius = fireButtonRect.width() / 2
            val distance = sqrt(
                ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)).toDouble()
            ).toFloat()
            if (distance <= radius) {
                isFiring = true
            } else {
                isFiring = false
                targetX = x.coerceIn(50f, 1030f)
                targetY = y.coerceIn(100f, 1800f)
            }
        } else if (event == MotionEvent.ACTION_UP) {
            isFiring = false
            targetX = null
            targetY = null
        }
    }

    override fun onPause() {
        soundManager.release()
        resourceLoader.release()
        fireIcon?.recycle()
        fireIcon = null
    }

    override fun onResume() {
        loadResources()
    }

    override fun backPressed() {
        game.actualScreen = StartScreen(game)
    }
}