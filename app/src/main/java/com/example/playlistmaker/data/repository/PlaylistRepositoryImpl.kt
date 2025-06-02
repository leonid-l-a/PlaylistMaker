package com.example.playlistmaker.data.repository

import androidx.room.Transaction
import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.TrackPlaylistsEntity
import com.example.playlistmaker.domain.entitie.PlaylistCreationData
import com.example.playlistmaker.domain.repository.PlaylistRepository

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override suspend fun createPlaylist(data: PlaylistCreationData) {
        val entity = PlaylistEntity(
            playlistName = data.name,
            playlistDescription = data.description,
            imagePath = data.imagePath,
        )
        playlistDao.insertPlaylist(entity)
    }

    override suspend fun addTrackToPlaylist(track: TrackPlaylistsEntity, playlistId: Long) {
        playlistDao.insertTrackAndUpdateCount(track, playlistId)
    }

    @Transaction
    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        playlistDao.removeTrackFromPlaylist(playlistId, trackId)

        val playlist = playlistDao.getPlaylistById(playlistId)
        playlistDao.updatePlaylist(playlist.copy(trackCount = playlist.trackCount - 1))
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylistWithCleanup(playlistId)
    }

    override suspend fun getPlaylists(): List<PlaylistEntity> {
        return playlistDao.getAllPlaylists()
    }
}
