package com.example.playlistmaker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.presentation.BaseActivity
import com.example.playlistmaker.presentation.settings.SettingsViewModel

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(this, Creator.provideSettingsViewModelFactory())[SettingsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.isDarkModeEnabled.observe(this) { isDarkModeEnabled ->
            binding.switchDarkMode.isChecked = isDarkModeEnabled
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.settingsScreenToolbar.setOnClickListener { finish() }
        binding.shareButton.setOnClickListener { viewModel.shareApp() }
        binding.supportButton.setOnClickListener { viewModel.sendSupportEmail() }
        binding.userAgreementButton.setOnClickListener { viewModel.openUserAgreement() }
        binding.switchDarkMode.setOnClickListener { viewModel.toggleDarkMode() }
    }
}
