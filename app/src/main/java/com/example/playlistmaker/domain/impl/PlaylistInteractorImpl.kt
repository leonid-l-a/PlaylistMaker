package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.TrackPlaylistsEntity
import com.example.playlistmaker.domain.entitie.Playlist
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.repository.PlaylistRepository

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository,
) : PlaylistInteractor {

    override suspend fun addTrackToPlaylist(
        track: TrackPlaylistsEntity,
        playlistId: Long,
    ): Boolean {
        return repository.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        repository.removeTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

    override suspend fun getAllPlaylists(): List<PlaylistEntity> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistWithTracks(playlistId: Long): Playlist {
        val playlist = repository.getPlaylistWithTracks(playlistId)
        val tracks = playlist.tracks.map { it.toTrack() }
        return Playlist(
            id = playlistId,
            name = playlist.playlist.playlistName,
            description = playlist.playlist.playlistDescription,
            imagePath = playlist.playlist.imagePath,
            tracks = tracks,
            trackCount = playlist.playlist.trackCount,
        )
    }

    fun TrackPlaylistsEntity.toTrack(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl,
        )
    }

    override suspend fun removeTrackFromPlaylistWithCleanup(playlistId: Long, trackId: Long) {
        repository.removeTrackFromPlaylistWithCleanup(playlistId, trackId)
    }
    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        return repository
            .getTracksForPlaylist(playlistId)
            .map { it.toTrack() }
    }
}
