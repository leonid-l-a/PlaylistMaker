package com.example.playlistmaker.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    favorites: List<Track>,
    playlists: List<PlaylistEntity>,
    onTrackClick: (Track) -> Unit,
    onNewPlaylistClick: () -> Unit,
    onPlaylistClick: (PlaylistEntity) -> Unit,
) {
    val tabs = listOf(
        stringResource(R.string.tab_favorites),
        stringResource(R.string.tab_playlists)
    )
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(false, stringResource(R.string.library)) { }

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.height(48.dp),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    color = MaterialTheme.colorScheme.onBackground,
                    height = 2.dp,
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                )
            },
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },

                    )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> {
                    if (favorites.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ph_library),
                                contentDescription = null,
                                modifier = Modifier.padding(top = 106.dp)
                            )
                            Text(
                                text = stringResource(R.string.empty_library),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Top
                        ) {
                            items(favorites) { track ->
                                TrackItem(track = track, onClick = onTrackClick)
                            }
                        }
                    }
                }

                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Button(
                            onClick = onNewPlaylistClick,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 24.dp),
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.onBackground,
                                contentColor = MaterialTheme.colorScheme.background,
                                disabledContainerColor = MaterialTheme.colorScheme.onBackground,
                                disabledContentColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text(text = stringResource(R.string.new_playlist))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        if (playlists.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ph_library),
                                    contentDescription = null,
                                    modifier = Modifier.padding(top = 46.dp)

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
    }
}
