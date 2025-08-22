package dev.ch8n.noteflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.ch8n.noteflow.data.YouTubeVideoDao
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import kotlinx.coroutines.flow.Flow

interface YouTubeVideoRepository {
    fun getVideosPaginated(): Flow<PagingData<YouTubeVideoEntity>>
    fun getVideosByQueryPaginated(query: String): Flow<PagingData<YouTubeVideoEntity>>
    suspend fun getVideoById(videoId: String): YouTubeVideoEntity?
    suspend fun insertVideo(video: YouTubeVideoEntity)
    suspend fun insertVideos(videos: List<YouTubeVideoEntity>)
    suspend fun updateVideo(video: YouTubeVideoEntity)
    suspend fun deleteVideo(video: YouTubeVideoEntity)
    suspend fun deleteAllVideos()
    suspend fun getVideosCount(): Int
    suspend fun getVideosByQueryCount(query: String): Int
}

class YouTubeVideoRepositoryImpl(
    private val youtubeVideoDao: YouTubeVideoDao
) : YouTubeVideoRepository {

    companion object {
        private const val PAGE_SIZE = 10
        private const val PREFETCH_DISTANCE = 5
        private const val INITIAL_LOAD_SIZE = 20
    }

    override fun getVideosPaginated(): Flow<PagingData<YouTubeVideoEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                initialLoadSize = INITIAL_LOAD_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { youtubeVideoDao.getVideosPagingSource() }
        ).flow
    }

    override fun getVideosByQueryPaginated(query: String): Flow<PagingData<YouTubeVideoEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                initialLoadSize = INITIAL_LOAD_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { youtubeVideoDao.getVideosByQueryPagingSource(query) }
        ).flow
    }

    override suspend fun getVideoById(videoId: String): YouTubeVideoEntity? {
        return youtubeVideoDao.getVideoById(videoId)
    }

    override suspend fun insertVideo(video: YouTubeVideoEntity) {
        youtubeVideoDao.insert(video)
    }

    override suspend fun insertVideos(videos: List<YouTubeVideoEntity>) {
        youtubeVideoDao.insertAll(videos)
    }

    override suspend fun updateVideo(video: YouTubeVideoEntity) {
        youtubeVideoDao.updateVideo(video)
    }

    override suspend fun deleteVideo(video: YouTubeVideoEntity) {
        youtubeVideoDao.deleteVideo(video)
    }

    override suspend fun deleteAllVideos() {
        youtubeVideoDao.deleteAll()
    }

    override suspend fun getVideosCount(): Int {
        return youtubeVideoDao.getVideosCount()
    }

    override suspend fun getVideosByQueryCount(query: String): Int {
        return youtubeVideoDao.getVideosByQueryCount(query)
    }
}
