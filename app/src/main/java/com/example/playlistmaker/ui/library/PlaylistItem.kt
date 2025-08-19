package com.example.playlistmaker.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.data.db.PlaylistEntity
import androidx.compose.foundation.layout.Spacer as ComposeSpacer

@Composable
fun PlaylistItem(playlist: PlaylistEntity, onClick: (PlaylistEntity) -> Unit) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick(playlist) },
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = playlist.imagePath,
            contentDescription = null,
            placeholder = androidx.compose.ui.res.painterResource(R.drawable.ph_no_image),
            error = androidx.compose.ui.res.painterResource(R.drawable.ph_no_image),
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        ComposeSpacer(modifier = Modifier.height(8.dp))
        Text(
            text = playlist.playlistName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = pluralStringResource(
                R.plurals.tracks_count,
                playlist.trackCount,
                playlist.trackCount
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
