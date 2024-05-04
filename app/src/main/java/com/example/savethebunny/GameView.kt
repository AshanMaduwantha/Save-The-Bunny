package com.example.savethebunny

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

@SuppressLint("ViewConstructor")
class GameView(var c: Context, private var gameTask: GameTask) : View(c) {

    private var myPaint: Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private var highScore = 0
    private var myCarPosition = 0
    private val otherCars = ArrayList<HashMap<String, Any>>()
    private var gameOver = false

    private var viewWidth = 0
    private var viewHeight = 0

    init {
        myPaint = Paint()
    }

    // Drawing the game elements
    @SuppressLint("DrawAllocation", "UseCompatLoadingForDrawables")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        // Add new cars at random intervals
        if (time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherCars.add(map)
        }
        time += 10 + speed
        val spikeWidth = viewWidth / 5
        val rabbitHeight = spikeWidth + 10
        myPaint!!.style = Paint.Style.FILL
        val rabbitDrawable = resources.getDrawable(R.drawable.rabbit, null)

        for (i in otherCars.indices) {
            try {
                val carX = otherCars[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                val carY = time - otherCars[i]["startTime"] as Int
                val spikeDrawable = resources.getDrawable(R.drawable.spike0, null)

                // Draw other cars
                spikeDrawable.setBounds(
                    carX + 25, carY - rabbitHeight, carX + spikeWidth - 25, carY
                )
                spikeDrawable.draw(canvas)

                // Check for collision with player's car
                if (otherCars[i]["lane"] as Int == myCarPosition) {
                    if (carY > viewHeight - 2 - rabbitHeight && carY < viewHeight - 2) {
                        gameTask.closeGame(score)
                    }
                }
                // Remove off-screen cars
                if (carY > viewHeight + rabbitHeight) {
                    otherCars.removeAt(i)
                    score++;
                    speed = 1 + Math.abs(score / 8)
                    if (score > highScore) {
                        highScore = score
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // Draw player's car (rabbit)
        rabbitDrawable.setBounds(
            myCarPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - rabbitHeight,
            myCarPosition * viewWidth / 3 + viewWidth / 15 + spikeWidth - 25,
            viewHeight - 2
        )
        rabbitDrawable.draw(canvas)

        // Draw score and high score
        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 40f
        canvas.drawText("Score:$score", 80f, 80f, myPaint!!)
        canvas.drawText("High Score:$highScore", 380f, 80f, myPaint!!)
        invalidate()
    }

    // Reset the game state
    fun resetGame() {
        score = 0
        speed = 1
        time = 0
        myCarPosition = 0
        otherCars.clear()
        invalidate()
        gameOver = false // Reset game over state
    }

    // Handle touch input to move the player's car
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if (gameOver) {
                    resetGame()
                } else {
                    // Determine lane of touch and move the player's car
                    val x1 = event.x
                    myCarPosition = when {
                        x1 < viewWidth / 3 -> 0
                        x1 < (viewWidth * 2) / 3 -> 1
                        else -> 2
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {}
        }
        return true
    }
}
