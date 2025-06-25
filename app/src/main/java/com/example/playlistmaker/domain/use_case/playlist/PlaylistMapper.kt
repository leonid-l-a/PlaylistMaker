package com.example.playlistmaker.domain.use_case.playlist

import com.example.playlistmaker.data.db.PlaylistWithTracks
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.TrackPlaylistsEntity
import com.example.playlistmaker.domain.entitie.Playlist

class PlaylistMapper {

    fun map(domain: Playlist): PlaylistWithTracks {
        val playlistEntity = PlaylistEntity(
            playlistId = domain.id,
            playlistName = domain.name,
            imagePath = domain.imagePath.orEmpty(),
            playlistDescription = domain.description.orEmpty()
        )

        val trackEntities = domain.tracks.map { domainTrack ->
            TrackPlaylistsEntity(
                trackId = domainTrack.trackId,
                trackName = domainTrack.trackName,
                artistName = domainTrack.artistName,
                trackTimeMillis = domainTrack.trackTimeMillis,
                artworkUrl100 = domainTrack.artworkUrl100,
                collectionName = domainTrack.collectionName,
                releaseDate = domainTrack.releaseDate,
                primaryGenreName = domainTrack.primaryGenreName,
                country = domainTrack.country,
                previewUrl = domainTrack.previewUrl
            )
        }

        return PlaylistWithTracks(
            playlist = playlistEntity,
            tracks = trackEntities
        )
    }
}
