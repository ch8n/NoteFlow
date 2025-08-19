package dev.ch8n.noteflow.ui.features.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    navigateToVideDetails: (video: YouTubeVideoEntity) -> Unit
) {

    val videoList by youtubeVideoListViewModel.videoList.collectAsState()
    val searchQuery by youtubeVideoListViewModel.searchQuery.collectAsState("")

    YouTubeVideoListContent(
        modifier = modifier,
        videos = videoList,
        searchQuery = searchQuery,
        updateQuery = youtubeVideoListViewModel::updateQuery,
        onVideoDetailsClicked = navigateToVideDetails
    )
}


class YouTubeVideoListViewModel(appDatabase: AppDatabase) : ViewModel() {
    private val youtubeVideoDao = appDatabase.youtubeVideoDao()
    val videoList = MutableStateFlow<List<YouTubeVideoEntity>>(emptyList())
    private val _searchQuery = MutableStateFlow<String>("")

    @OptIn(FlowPreview::class)
    val searchQuery = _searchQuery
        .debounce(500)
        .distinctUntilChanged()
        .onEach { query ->
            if (query.isBlank()) {
                loadVideos()
            } else {
                filterVideos(query)
            }
        }

    fun updateQuery(query: String) {
        _searchQuery.update { query }
    }

    private fun loadVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            val videos = youtubeVideoDao.getAllVideos()
            videoList.update { videos }
        }
    }

    private fun filterVideos(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val videos = youtubeVideoDao.getAllVideosByQuery(query)
            videoList.update { videos }
        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YouTubeVideoListContent(
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    updateQuery: (query: String) -> Unit = {},
    videos: List<YouTubeVideoEntity>,
    onVideoDetailsClicked: (video: YouTubeVideoEntity) -> Unit = {}
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
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        label = { Text("Search") },
                        onValueChange = updateQuery,
                        modifier = Modifier.fillMaxWidth(),
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
