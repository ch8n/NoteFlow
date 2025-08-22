package dev.ch8n.noteflow.ui.features.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.ui.features.details.YouTubeVideoDetail


@Composable
fun YoutubeSearchScreen(
    modifier: Modifier = Modifier,
    youtubeVideoListViewModel: YouTubeVideoListViewModel,
    navigateToVideDetails: (video: YouTubeVideoEntity) -> Unit,
    navigateToSettings: () -> Unit
) {

    val videoPagingItems = youtubeVideoListViewModel.videoPagingData.collectAsLazyPagingItems()
    val searchQuery by youtubeVideoListViewModel.searchQuery.collectAsState("")

    YouTubeVideoListContent(
        modifier = modifier,
        videoPagingItems = videoPagingItems,
        searchQuery = searchQuery,
        updateQuery = youtubeVideoListViewModel::updateSearchQuery,
        onVideoDetailsClicked = navigateToVideDetails,
        onSettingsClicked = navigateToSettings
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YouTubeVideoListContent(
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    updateQuery: (query: String) -> Unit = {},
    videoPagingItems: LazyPagingItems<YouTubeVideoEntity>,
    onVideoDetailsClicked: (video: YouTubeVideoEntity) -> Unit = {},
    onSettingsClicked: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stickyHeader {
            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        label = { Text("Search") },
                        onValueChange = updateQuery,
                        modifier = Modifier.fillMaxWidth(0.85f),
                        trailingIcon = {
                            IconButton(onClick = {
                                updateQuery.invoke("")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Clear Search",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    )

                    IconButton(onClick = onSettingsClicked) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        repeat(videoPagingItems.itemCount) { index ->
            val video = videoPagingItems[index]
            YouTubeVideoDetail(
                video = video,
                onVideClicked = { videoItem ->
                    onVideoDetailsClicked.invoke(videoItem)
                }
            )
        }

        item {
            when (val loadState = videoPagingItems.loadState.append) {
                is LoadState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is LoadState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${loadState.error.localizedMessage}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is LoadState.NotLoading -> {
                    // No additional loading indicator needed
                }
            }
        }

        // Handle initial loading state
        if (videoPagingItems.loadState.refresh is LoadState.Loading && videoPagingItems.itemCount == 0) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Handle initial error state
        if (videoPagingItems.loadState.refresh is LoadState.Error && videoPagingItems.itemCount == 0) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error loading videos: ${(videoPagingItems.loadState.refresh as LoadState.Error).error.localizedMessage}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
