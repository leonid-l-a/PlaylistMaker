package com.example.playlistmaker.domain.use_case.inter

import android.content.Context
import android.net.Uri

interface SaveImageUseCase {
    suspend operator fun invoke(uri: Uri, context: Context): String
}
