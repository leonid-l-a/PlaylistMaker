package com.example.playlistmaker.presentation

import android.os.Bundle
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.playlistmaker.R

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarAppearance()
    }

    private fun setStatusBarAppearance() {
        val backgroundColor = ContextCompat.getColor(this, R.color.common_screen_background_tint)

        window.statusBarColor = backgroundColor

        val isDarkBackground = isDarkColor(backgroundColor)

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        insetsController.isAppearanceLightStatusBars = !isDarkBackground
    }

    private fun isDarkColor(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }
}
