package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE)

        val isDarkMode = sharedPreferences.getBoolean("IS_DARK_MODE", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding.buttonSearchActivity.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.buttonLibraryActivity.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        binding.buttonSettingsActivity.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        val color = ContextCompat.getColor(this, R.color.main_activity_background_tint)

        window.statusBarColor = color
    }
}