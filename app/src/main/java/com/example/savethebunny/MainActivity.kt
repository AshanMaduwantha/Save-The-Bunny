package com.example.savethebunny

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), GameTask {

    // UI elements
    private lateinit var rootLayout: LinearLayout
    private lateinit var startBtn: Button
    private lateinit var restartBtn: Button
    private lateinit var mGameView: GameView
    private lateinit var score: TextView
    private lateinit var highScoreTextView: TextView

    // High score
    private var highScore = 0

    // SharedPreferences for high score persistence
    private lateinit var sharedPreferences: SharedPreferences

    // MediaPlayer for background music
    private var mediaPlayer: MediaPlayer? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences for high score storage
        sharedPreferences = getSharedPreferences("HighScorePrefs", Context.MODE_PRIVATE)
        // Retrieve the high score from SharedPreferences, default to 0 if not found
        highScore = sharedPreferences.getInt("highScore", 0)

        // Initialize UI elements
        startBtn = findViewById(R.id.startBtn)
        restartBtn = findViewById(R.id.restartBtn)
        rootLayout = findViewById(R.id.rootLayout)
        score = findViewById(R.id.score)
        highScoreTextView = findViewById(R.id.highScore)
        mGameView = GameView(this, this)

        // Set the text for the high score TextView
        highScoreTextView.text = "High Score : $highScore"

        // Set click listeners for buttons
        startBtn.setOnClickListener {
            startGame()
        }

        restartBtn.setOnClickListener {
            restartGame()
        }
    }

    // Function to start the game
    private fun startGame() {
        // Start background music
        startBackgroundMusic()

        // Set background resource for the game view
        mGameView.setBackgroundResource(R.drawable.background)
        // Add the game view to the root layout
        rootLayout.addView(mGameView)
        // Hide buttons and text views
        startBtn.visibility = View.GONE
        restartBtn.visibility = View.GONE
        score.visibility = View.GONE
        highScoreTextView.visibility = View.GONE
    }

    // Function to restart the game
    private fun restartGame() {
        // Reset the game view
        mGameView.resetGame()
        // Stop background music
        stopBackgroundMusic()
        // Start the game again
        startGame()
    }

    // Function called when the game is closed
    @SuppressLint("SetTextI18n")
    override fun closeGame(mScore: Int) {
        // Update score text view
        score.text = "Score : $mScore"
        // Remove the game view from the root layout
        rootLayout.removeView(mGameView)
        // Show buttons and text views
        startBtn.visibility = View.VISIBLE
        restartBtn.visibility = View.VISIBLE
        score.visibility = View.VISIBLE
        highScoreTextView.visibility = View.VISIBLE
        // Update high score if necessary
        if (mScore > highScore) {
            highScore = mScore
            // Update high score text view
            highScoreTextView.text = "High Score : $highScore"
            // Save the new high score to SharedPreferences
            sharedPreferences.edit().putInt("highScore", highScore).apply()
        }
        // Stop background music
        stopBackgroundMusic()
    }

    // Function to start the background music
    private fun startBackgroundMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.bgmusic)
        mediaPlayer?.isLooping = true // Loop the music
        mediaPlayer?.start() // Start playing
    }

    // Function to stop the background music
    private fun stopBackgroundMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources
        stopBackgroundMusic()
    }
}
