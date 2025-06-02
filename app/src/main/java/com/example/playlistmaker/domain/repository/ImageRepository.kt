package com.example.playlistmaker.domain.repository

import android.content.Context
import android.net.Uri

interface ImageRepository {
    suspend fun saveToInternalStorage(uri: Uri, context: Context): String
}
