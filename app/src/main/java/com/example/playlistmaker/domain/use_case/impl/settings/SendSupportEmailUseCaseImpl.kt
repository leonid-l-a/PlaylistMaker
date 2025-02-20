package com.example.playlistmaker.domain.use_case.impl.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R

class SendSupportEmailUseCaseImpl(private val context: Context) : SendSupportEmailUseCase {
    override fun sendSupportEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_text))
        }
        val chooserIntent = Intent.createChooser(emailIntent, context.getString(R.string.text_to_support)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooserIntent)
    }
}
