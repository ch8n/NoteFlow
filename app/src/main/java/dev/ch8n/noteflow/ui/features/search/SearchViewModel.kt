package dev.ch8n.noteflow.ui.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ch8n.noteflow.data.AppDatabase
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


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

