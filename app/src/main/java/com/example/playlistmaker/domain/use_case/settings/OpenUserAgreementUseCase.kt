package com.example.playlistmaker.domain.use_case.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R

class OpenUserAgreementUseCase(private val context: Context) {
    fun openUserAgreement() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.user_agreement_uri))).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(browserIntent)
    }
}
