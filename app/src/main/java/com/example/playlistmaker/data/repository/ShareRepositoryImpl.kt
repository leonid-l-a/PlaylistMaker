package com.example.playlistmaker.data.repository

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.R
import com.example.playlistmaker.data.db.PlaylistWithTracks
import com.example.playlistmaker.domain.repository.ShareRepository

class ShareRepositoryImpl(
    private val context: Context,
) : ShareRepository {

    override suspend fun share(playlist: PlaylistWithTracks) {
        val message = buildShareMessage(playlist)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        val chooser =
            Intent.createChooser(shareIntent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooser)
    }

    private fun buildShareMessage(p: PlaylistWithTracks): String {
        val sb = StringBuilder()
        sb.appendLine(p.playlist.playlistName)
        if (!p.playlist.playlistDescription.isNullOrEmpty()) {
            sb.appendLine(p.playlist.playlistDescription)
        }
        val countText = context.resources.getQuantityString(
            R.plurals.tracks_count,
            p.tracks.size,
            p.tracks.size
        )
        sb.appendLine(countText)
        sb.appendLine()

        p.tracks.forEachIndexed { index, track ->
            sb.append("${index + 1}. ")
            sb.append(track.artistName)
            sb.append(" - ")
            sb.append(track.trackName)
            sb.append(" (")
            sb.append(track.trackTimeMillis)
            sb.appendLine(")")
        }

        return sb.toString().trimEnd()
    }
}
