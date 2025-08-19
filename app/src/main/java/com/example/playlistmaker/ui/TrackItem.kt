package com.example.playlistmaker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Spacer as ComposeSpacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.ui.theme.Dimens

@Composable
fun TrackItem(track: Track, onClick: (Track) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.BigLineHeight)
            .clickable { onClick(track) }
            .padding(horizontal = 13.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ph_no_image),
            error = painterResource(R.drawable.ph_no_image),
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ComposeSpacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    painter = painterResource(R.drawable.ic_dot),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = track.trackTimeMillis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Icon(
            painter = painterResource(R.drawable.ic_arrow_forward),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}