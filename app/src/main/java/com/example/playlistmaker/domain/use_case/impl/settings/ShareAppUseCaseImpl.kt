package com.example.playlistmaker.domain.use_case.impl.settings

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.R

class ShareAppUseCaseImpl(private val context: Context) : ShareAppUseCase {
    override fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text))
        }
        val chooserIntent = Intent.createChooser(shareIntent, context.getString(R.string.share_app)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooserIntent)
    }
}