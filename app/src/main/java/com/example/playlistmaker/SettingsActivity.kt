package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import com.google.android.material.appbar.MaterialToolbar
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import android.widget.TextView
class SettingsActivity : AppCompatActivity() {
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
            putExtra(Intent.EXTRA_TEXT, "Изучайте Android-разработку с Яндекс Практикумом: https://practicum.yandex.ru/android-developer/")
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
    }

    private fun sendSupportEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Только email-клиенты
            putExtra(Intent.EXTRA_EMAIL, arrayOf("leo.lapaeff@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Сообщение разработчикам и разработчицам приложения Playlist Maker")
            putExtra(Intent.EXTRA_TEXT, "Спасибо разработчикам и разработчицам за крутое приложение!")
        }
        startActivity(Intent.createChooser(emailIntent, "Написать в поддержку"))
    }

    private fun openUserAgreement() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/practicum_offer/"))
        startActivity(browserIntent)
    }
}
