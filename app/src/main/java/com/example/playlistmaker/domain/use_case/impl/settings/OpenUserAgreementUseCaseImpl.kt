package com.example.playlistmaker.domain.use_case.impl.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R

class OpenUserAgreementUseCaseImpl(private val context: Context) : OpenUserAgreementUseCase {
    override fun openUserAgreement() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.user_agreement_uri))).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(browserIntent)
    }
}
