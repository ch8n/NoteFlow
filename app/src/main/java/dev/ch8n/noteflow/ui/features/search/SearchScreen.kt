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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ch8n.noteflow.data.AppDatabase
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.ui.features.details.YouTubeVideoDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@Composable
fun YoutubeSearchScreen(
    modifier: Modifier = Modifier,
    youtubeVideoListViewModel: YouTubeVideoListViewModel,
    onVideoDetailsClicked: (video: YouTubeVideoEntity) -> Unit
) {

    LaunchedEffect(Unit) {
        youtubeVideoListViewModel.loadVideos()
    }

    val videoList by youtubeVideoListViewModel.videoList.collectAsState()

    YouTubeVideoListContent(
        modifier = modifier,
        videos = videoList,
        onVideoDetailsClicked = { video ->
            onVideoDetailsClicked.invoke(video)
        }
    )
}


class YouTubeVideoListViewModel(appDatabase: AppDatabase) : ViewModel() {
    private val youtubeVideoDao = appDatabase.youtubeVideoDao()

    val videoList = MutableStateFlow<List<YouTubeVideoEntity>>(emptyList())

    fun loadVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            val videos = youtubeVideoDao.getAllVideos()
            videoList.update { videos }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YouTubeVideoListContent(
    modifier: Modifier = Modifier,
    videos: List<YouTubeVideoEntity>,
    onVideoDetailsClicked: (video: YouTubeVideoEntity) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredVideos = remember(searchQuery, videos) {
        if (searchQuery.isBlank()) videos
        else videos.filter {
            it.title?.contains(searchQuery, ignoreCase = true) == true ||
                    it.description?.contains(searchQuery, ignoreCase = true) == true
        }
    }

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
                        onValueChange = { searchQuery = it },
                        label = { Text("Search") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = {
                                searchQuery = ""
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

        repeat(filteredVideos.size) { index ->
            YouTubeVideoDetail(
                filteredVideos.get(index),
                onVideClicked = {video ->
                    onVideoDetailsClicked.invoke(video)
                }
            )
        }
    }
}
