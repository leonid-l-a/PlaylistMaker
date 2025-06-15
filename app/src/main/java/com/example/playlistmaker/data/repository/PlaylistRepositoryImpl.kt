package com.example.playlistmaker.data.repository

import androidx.room.Transaction
import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.PlaylistWithTracks
import com.example.playlistmaker.data.db.TrackPlaylistsEntity
import com.example.playlistmaker.domain.entitie.PlaylistCreationData
import com.example.playlistmaker.domain.repository.PlaylistRepository

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
) : PlaylistRepository {

    override suspend fun createPlaylist(data: PlaylistCreationData) {
        val entity = PlaylistEntity(
            playlistName = data.name,
            playlistDescription = data.description,
            imagePath = data.imagePath,
        )
        playlistDao.insertPlaylist(entity)
    }

    override suspend fun addTrackToPlaylist(
        track: TrackPlaylistsEntity,
        playlistId: Long,
    ): Boolean {
        return playlistDao.insertTrackAndUpdateCount(track, playlistId)
    }

    @Transaction
    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        playlistDao.removeTrackFromPlaylist(playlistId, trackId)

        val playlist = playlistDao.getPlaylistById(playlistId)
        playlistDao.updatePlaylist(playlist.copy(trackCount = playlist.trackCount - 1))
    }

    override suspend fun getPlaylistById(playlistId: Long): PlaylistEntity {
        return playlistDao.getPlaylistById(playlistId)
    }

    override suspend fun updatePlaylist(playlist: PlaylistEntity) {
        playlistDao.updatePlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylistWithCleanup(playlistId)
    }

    override suspend fun getPlaylists(): List<PlaylistEntity> {
        return playlistDao.getAllPlaylists()
    }

    override suspend fun getPlaylistWithTracks(trackId: Long): PlaylistWithTracks {
        return playlistDao.getPlaylistWithTracks(trackId)
    }

    override suspend fun removeTrackFromPlaylistWithCleanup(playlistId: Long, trackId: Long) {
        playlistDao.removeTrackFromPlaylistWithCleanup(playlistId, trackId)
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<TrackPlaylistsEntity> {
        return playlistDao.getTracksForPlaylist(playlistId)
    }
}
