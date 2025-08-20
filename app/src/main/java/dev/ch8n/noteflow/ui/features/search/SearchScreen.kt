package dev.ch8n.noteflow.ui.features.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ch8n.noteflow.data.AppDatabase
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.ui.features.details.YouTubeVideoDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@Composable
fun YoutubeSearchScreen(
    modifier: Modifier = Modifier,
    youtubeVideoListViewModel: YouTubeVideoListViewModel,
    navigateToVideDetails: (video: YouTubeVideoEntity) -> Unit,
    navigateToSettings: () -> Unit
) {

    val videoList by youtubeVideoListViewModel.videoList.collectAsState()
    val searchQuery by youtubeVideoListViewModel.searchQuery.collectAsState("")

    YouTubeVideoListContent(
        modifier = modifier,
        videos = videoList,
        searchQuery = searchQuery,
        updateQuery = youtubeVideoListViewModel::updateQuery,
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
    videos: List<YouTubeVideoEntity>,
    onVideoDetailsClicked: (video: YouTubeVideoEntity) -> Unit = {},
    onSettingsClicked: () -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
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

        repeat(videos.size) { index ->
            YouTubeVideoDetail(
                video = videos.get(index),
                onVideClicked = { video ->
                    onVideoDetailsClicked.invoke(video)
                }
            )
        }
    }
}
