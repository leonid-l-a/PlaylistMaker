package com.example.playlistmaker.presentation.library

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.entitie.PlaylistCreationData
import com.example.playlistmaker.domain.use_case.inter.CreatePlaylistUseCase
import com.example.playlistmaker.domain.use_case.inter.SaveImageUseCase
import com.example.playlistmaker.domain.use_case.playlist.UpdatePlaylistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class PlaylistCreationViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val saveImageUseCase: SaveImageUseCase,
    private val updatePlaylistUseCase: UpdatePlaylistUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PlaylistCreationState())
    val state: StateFlow<PlaylistCreationState> = _state.asStateFlow()

    private val _playlistCreationState = MutableStateFlow<Result<Unit>?>(null)
    val playlistCreationState: StateFlow<Result<Unit>?> = _playlistCreationState

    fun onImageSelected(uri: Uri) {
        _state.value = _state.value.copy(selectedImageUri = uri)
    }

    fun createPlaylist(name: String, description: String?, context: Context) {
        viewModelScope.launch {
            try {
                val localPath = state.value.selectedImageUri?.let { uri ->
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

    fun updatePlaylist(name: String, description: String?, context: Context) {
        viewModelScope.launch {
            val currentPlaylist = _state.value.playlist ?: return@launch

            val newImagePath = state.value.selectedImageUri?.let { uri ->
                if (uri.toString() != currentPlaylist.imagePath) {
                    saveImageUseCase(uri, context)
                } else {
                    currentPlaylist.imagePath
                }
            }

            val updatedPlaylist = currentPlaylist.copy(
                playlistName = name,
                playlistDescription = description,
                imagePath = newImagePath
            )

            updatePlaylistUseCase.updatePlaylist(updatedPlaylist)

            _state.update { it.copy(playlist = updatedPlaylist) }
            _playlistCreationState.value = Result.success(Unit)
        }
    }


    fun setPlaylist(playlist: PlaylistEntity) {
        _state.update { old ->
            old.copy(playlist = playlist,
                selectedImageUri = playlist.imagePath?.let(Uri::parse))
        }
    }
}
