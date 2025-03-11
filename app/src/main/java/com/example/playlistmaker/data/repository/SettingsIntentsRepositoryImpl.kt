package com.example.playlistmaker.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.repository.SettingsIntentsRepository

class SettingsIntentsRepositoryImpl(private val context: Context) : SettingsIntentsRepository{
    override fun openUserAgreement() {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            context.getString(R.string.user_agreement_uri).toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(browserIntent)
    }

    override fun sendSupportEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_text))
        }
        val chooserIntent = Intent.createChooser(
            emailIntent,
            context.getString(R.string.text_to_support)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooserIntent)
    }

    override fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text))
        }
        val chooserIntent = Intent.createChooser(
            shareIntent,
            context.getString(R.string.share_app)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooserIntent)
    }
}