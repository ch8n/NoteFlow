package dev.ch8n.noteflow.ui.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.ch8n.noteflow.data.AppDatabase
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.data.repository.YouTubeVideoRepository
import dev.ch8n.noteflow.data.repository.YouTubeVideoRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class YouTubeVideoListViewModel(appDatabase: AppDatabase) : ViewModel() {

    private val repository: YouTubeVideoRepository = YouTubeVideoRepositoryImpl(appDatabase.youtubeVideoDao())
    
    val searchQuery = MutableStateFlow<String>("")
    
    val videoPagingData: Flow<PagingData<YouTubeVideoEntity>> = searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getVideosPaginated()
            } else {
                repository.getVideosByQueryPaginated(query)
            }
        }
        .cachedIn(viewModelScope)

    fun updateSearchQuery(query: String) {
        searchQuery.update { query }
    }

    fun insertVideo(video: YouTubeVideoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertVideo(video)
        }
    }

    fun insertVideos(videos: List<YouTubeVideoEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertVideos(videos)
        }
    }

    fun updateVideo(video: YouTubeVideoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateVideo(video)
        }
    }

    fun deleteVideo(video: YouTubeVideoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteVideo(video)
        }
    }

    suspend fun getVideoById(videoId: String): YouTubeVideoEntity? {
        return repository.getVideoById(videoId)
    }

}

