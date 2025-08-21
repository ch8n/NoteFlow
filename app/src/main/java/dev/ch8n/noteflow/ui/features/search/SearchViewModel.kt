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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class YouTubeVideoListViewModel(appDatabase: AppDatabase) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    private val youtubeVideoDao = appDatabase.youtubeVideoDao()
    val videoList = MutableStateFlow<List<YouTubeVideoEntity>>(emptyList())
    val searchQuery = MutableStateFlow<String>("")

    init {
        searchQuery.debounce(500)
            .distinctUntilChanged()
            .onEach { query ->
                resetIndex()
                loadNextVideo()
            }
            .launchIn(viewModelScope)
    }

    private val pageIndex = MutableStateFlow<Int>(0)


    fun updateQuery(query: String) {
        searchQuery.update { query }
    }

    fun resetIndex() {
        pageIndex.update { 0 }
    }

    fun incrementIndex() {
        pageIndex.update { it + 1 }
    }

    fun loadNextVideo() {
        viewModelScope.launch(Dispatchers.IO) {
            val offset = (pageIndex.value + 1) * PAGE_SIZE
            val query = searchQuery.value
            val videos = if (query.isNotEmpty()) {
                youtubeVideoDao.getVideosByQueryPaginated(query, PAGE_SIZE, offset)
            } else {
                youtubeVideoDao.getVideosPaginated(PAGE_SIZE, offset)
            }
            videoList.update { videos }
        }
    }

}

