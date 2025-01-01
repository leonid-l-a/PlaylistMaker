package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE)

        binding.settingsScreenToolbar.setOnClickListener {
            finish()
        }

        binding.shareButton.setOnClickListener {
            shareApp()
        }

        binding.supportButton.setOnClickListener {
            sendSupportEmail()
        }

        binding.userAgreementButton.setOnClickListener {
            openUserAgreement()
        }

        val isDarkMode = sharedPreferences.getBoolean("IS_DARK_MODE", false)
        binding.switchDarkMode.isChecked = isDarkMode

        binding.switchDarkMode.setOnClickListener {
            val newMode = if (binding.switchDarkMode.isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(newMode)

            sharedPreferences.edit().putBoolean("IS_DARK_MODE", binding.switchDarkMode.isChecked).apply()
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
    }

    private fun sendSupportEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, getString(R.string.support_email))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_text))
        }
        startActivity(Intent.createChooser(emailIntent, getString(R.string.text_to_support)))
    }

    private fun openUserAgreement() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_uri)))
        startActivity(browserIntent)
    }
}