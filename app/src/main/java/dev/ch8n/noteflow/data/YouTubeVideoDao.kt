package dev.ch8n.noteflow.data

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert


@Entity(tableName = "youtube_videos")
data class YouTubeVideoEntity(
    @PrimaryKey val videoId: String,
    val videoUrl: String?,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val transcription: String?,
    val aiDigest: String?,
    val createdAt: Long?
) {
    companion object {
        val Empty = YouTubeVideoEntity(
            videoId = "",
            videoUrl = null,
            title = null,
            description = null,
            thumbnailUrl = null,
            transcription = null,
            aiDigest = null,
            createdAt = null
        )
    }
}


@Dao
interface YouTubeVideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: YouTubeVideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videos: List<YouTubeVideoEntity>)

    @Query("SELECT * FROM youtube_videos WHERE videoId = :id")
    suspend fun getVideoById(id: String): YouTubeVideoEntity?

    // Paging 3 methods
    @Query("SELECT * FROM youtube_videos ORDER BY createdAt DESC")
    fun getVideosPagingSource(): PagingSource<Int, YouTubeVideoEntity>

    @Query("SELECT * FROM youtube_videos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR transcription LIKE '%' || :query || '%' OR aiDigest LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun getVideosByQueryPagingSource(query: String): PagingSource<Int, YouTubeVideoEntity>

    @Upsert
    suspend fun updateVideo(video: YouTubeVideoEntity)

    @Delete
    suspend fun deleteVideo(video: YouTubeVideoEntity)

    @Query("DELETE FROM youtube_videos")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM youtube_videos")
    suspend fun getVideosCount(): Int

    @Query("SELECT COUNT(*) FROM youtube_videos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR transcription LIKE '%' || :query || '%' OR aiDigest LIKE '%' || :query || '%'")
    suspend fun getVideosByQueryCount(query: String): Int
}


@Database(entities = [YouTubeVideoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun youtubeVideoDao(): YouTubeVideoDao
}

object DatabaseProvider {
    fun getDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "youtube_video_db"
        ).build()
    }
}