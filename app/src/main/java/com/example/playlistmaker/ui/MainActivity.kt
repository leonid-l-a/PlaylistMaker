package com.example.playlistmaker.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.domain.use_case.main.SetCurrentModeUseCase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsInteractor: SettingsInteractor
    private lateinit var setCurrentModeUseCase: SetCurrentModeUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settingsInteractor = Creator.provideSettingsInteractor()

        val isDarkMode = settingsInteractor.getSettings().isDarkModeEnabled

        setCurrentModeUseCase = Creator.provideSetCurrentModeUseCase()
        setCurrentModeUseCase.setCurrentMode(isDarkMode)


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