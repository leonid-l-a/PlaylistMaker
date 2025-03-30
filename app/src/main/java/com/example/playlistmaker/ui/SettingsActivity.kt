package com.example.playlistmaker.ui

import android.os.Bundle
import android.util.Log
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.presentation.BaseActivity
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("ONCREATEACTIVITY", "ISCALLED")

        viewModel.isDarkModeEnabled.observe(this) { isDarkModeEnabled ->
            binding.switchDarkMode.isChecked = isDarkModeEnabled
        }

        binding.settingsScreenToolbar.setOnClickListener { finish() }
        binding.shareButton.setOnClickListener { viewModel.shareApp() }
        binding.supportButton.setOnClickListener { viewModel.sendSupportEmail() }
        binding.userAgreementButton.setOnClickListener { viewModel.openUserAgreement() }
        binding.switchDarkMode.setOnClickListener { viewModel.toggleDarkMode() }
    }
}