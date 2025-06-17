package com.example.playlistmaker.presentation.library

import android.net.Uri
import com.example.playlistmaker.data.db.PlaylistEntity

data class PlaylistCreationState(
    val playlist: PlaylistEntity? = null,
    val selectedImageUri: Uri? = null

)