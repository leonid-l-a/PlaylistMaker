package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import com.google.android.material.appbar.MaterialToolbar
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton: MaterialToolbar = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val shareButton: TextView = findViewById(R.id.share_button)
        shareButton.setOnClickListener {
            shareApp()
        }

        val supportButton: TextView = findViewById(R.id.support_button)
        supportButton.setOnClickListener {
            sendSupportEmail()
        }

        val userAgreementButton: TextView = findViewById(R.id.user_agreement)
        userAgreementButton.setOnClickListener {
            openUserAgreement()
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.share_text)
            )
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
    }

    private fun sendSupportEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Только email-клиенты
            putExtra(Intent.EXTRA_EMAIL, getString(R.string.support_email))
            putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.support_subject)
            )
            putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.support_text)
            )
        }
        startActivity(Intent.createChooser(emailIntent, "Написать в поддержку"))
    }

    private fun openUserAgreement() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_uri)))
        startActivity(browserIntent)
    }

    private fun setAppTheme(isDarkModeEnabled: Boolean) {
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
