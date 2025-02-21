package com.example.playlistmaker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.domain.use_case.impl.settings.OpenUserAgreementUseCase
import com.example.playlistmaker.domain.use_case.impl.settings.SendSupportEmailUseCase
import com.example.playlistmaker.domain.use_case.impl.settings.ShareAppUseCase
import com.example.playlistmaker.domain.use_case.impl.settings.ToggleDarkModeUseCase
import com.example.playlistmaker.presentation.BaseActivity

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsInteractor: SettingsInteractor
    private lateinit var shareAppUseCase: ShareAppUseCase
    private lateinit var sendSupportEmailUseCase: SendSupportEmailUseCase
    private lateinit var openUserAgreementUseCase: OpenUserAgreementUseCase
    private lateinit var toggleDarkModeUseCase: ToggleDarkModeUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settingsInteractor = Creator.provideSettingsInteractor()
        shareAppUseCase = Creator.provideShareAppUseCase()
        sendSupportEmailUseCase = Creator.provideSendSupportEmailUseCase()
        openUserAgreementUseCase = Creator.provideOpenUserAgreementUseCase()
        toggleDarkModeUseCase = Creator.provideToggleDarkModeUseCase()


        val settings = settingsInteractor.getSettings()
        binding.switchDarkMode.isChecked = settings.isDarkModeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (settings.isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        binding.settingsScreenToolbar.setOnClickListener {
            finish()
        }

        binding.shareButton.setOnClickListener { shareAppUseCase.shareApp() }
        binding.supportButton.setOnClickListener { sendSupportEmailUseCase.sendSupportEmail() }
        binding.userAgreementButton.setOnClickListener { openUserAgreementUseCase.openUserAgreement() }

        binding.switchDarkMode.setOnClickListener {
            val newState = toggleDarkModeUseCase.toggleDarkMode()
            AppCompatDelegate.setDefaultNightMode(
                if (newState) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            recreate()
        }
    }
}
