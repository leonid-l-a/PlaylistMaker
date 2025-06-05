package com.example.playlistmaker.domain.use_case.settings.impl

import android.content.Context
import android.net.Uri
import com.example.playlistmaker.domain.repository.ImageRepository
import com.example.playlistmaker.domain.use_case.settings.inter.SaveImageUseCase

class SaveImageUseCaseImpl(
    private val repository: ImageRepository
) : SaveImageUseCase {
    override suspend fun invoke(uri: Uri, context: Context): String {
        return repository.saveToInternalStorage(uri, context)
    }
}