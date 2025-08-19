package com.example.playlistmaker.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.ui.TrackItem
import com.example.playlistmaker.ui.util.AppTopBar

@Composable
fun LibraryScreen(
    favorites: List<Track>,
    playlists: List<PlaylistEntity>,
    onTrackClick: (Track) -> Unit,
    onNewPlaylistClick: () -> Unit,
    onPlaylistClick: (PlaylistEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(false, stringResource(R.string.library)) {}
        val tabs = listOf(
            stringResource(R.string.tab_favorites),
            stringResource(R.string.tab_playlists)
        )
        var selectedTab by remember { mutableIntStateOf(0) }
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = (selectedTab == index),
                    onClick = { selectedTab = index }
                )
            }
        }
        if (selectedTab == 0) {
            if (favorites.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ph_library),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.empty_library),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favorites) { track ->
                        TrackItem(track = track, onClick = onTrackClick)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onNewPlaylistClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.new_playlist))
                }
                Spacer(modifier = Modifier.height(24.dp))
                if (playlists.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ph_library),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.no_playlists_created),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(playlists) { playlist ->
                            PlaylistItem(
                                playlist = playlist,
                                onClick = onPlaylistClick
                            )
                        }
                    }
                }
            }
        }
    }
}
