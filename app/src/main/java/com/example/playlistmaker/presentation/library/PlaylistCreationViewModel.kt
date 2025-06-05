package com.example.playlistmaker.presentation.library

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.entitie.PlaylistCreationData
import com.example.playlistmaker.domain.use_case.settings.inter.CreatePlaylistUseCase
import com.example.playlistmaker.domain.use_case.settings.inter.SaveImageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistCreationViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val saveImageUseCase: SaveImageUseCase
) : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _playlistCreationState = MutableStateFlow<Result<Unit>?>(null)
    val playlistCreationState: StateFlow<Result<Unit>?> = _playlistCreationState

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun createPlaylist(name: String, description: String?, context: Context) {
        viewModelScope.launch {
            try {
                val localPath = _selectedImageUri.value?.let { uri ->
                    saveImageUseCase(uri, context)
                }

                val data = PlaylistCreationData(
                    name = name,
                    description = description,
                    imagePath = localPath
                )

                createPlaylistUseCase(data)

                _playlistCreationState.value = Result.success(Unit)
            } catch (e: Exception) {
                _playlistCreationState.value = Result.failure(e)
            }
        }
    }
}

