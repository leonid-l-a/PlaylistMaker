package com.example.playlistmaker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.presentation.search.SearchState
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.ui.util.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit,
) {
    val searchState by viewModel.searchState.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        AppTopBar(false, stringResource(R.string.search)) {}

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = query,
            onValueChange = { text ->
                query = text
                viewModel.onQueryChanged(text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(0.dp),
            singleLine = true,
            maxLines = 1,
            placeholder = { Text(stringResource(R.string.search)) },
            textStyle = MaterialTheme.typography.bodyLarge,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = {
                        query = ""
                        viewModel.loadHistory()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_button_clear),
                            contentDescription = stringResource(R.string.clear),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        when (val state = searchState) {
            is SearchState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is SearchState.Success -> {
                if (state.tracks.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.tracks) { track ->
                            TrackItem(track = track, onClick = onTrackClick)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(102.dp))
                        Image(
                            painter = painterResource(R.drawable.ph_nothing_found),
                            contentDescription = stringResource(R.string.nothing_found)
                        )
                        Text(
                            text = stringResource(R.string.nothing_found),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }

            is SearchState.Error -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(102.dp))
                    Image(
                        painter = painterResource(R.drawable.ph_no_connection),
                        contentDescription = stringResource(R.string.no_connection_text)
                    )
                    Text(
                        text = stringResource(R.string.no_connection_text),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { viewModel.retryLastSearch() }) {
                        Text(text = stringResource(R.string.retrySearch))
                    }
                }
            }

            is SearchState.History -> {
                val historyList = state.tracks
                if (historyList.isNotEmpty()) {
                    Column {
                        Text(
                            text = stringResource(R.string.search_history),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(historyList) { track ->
                                TrackItem(track = track, onClick = onTrackClick)
                            }
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = { viewModel.clearHistory() },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(text = stringResource(R.string.clear_history))
                        }
                    }
                }
            }

            is SearchState.Empty -> {
            }
        }
    }
}

