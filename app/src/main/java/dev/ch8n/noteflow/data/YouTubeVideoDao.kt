package dev.ch8n.noteflow.data

import android.content.Context
import androidx.room.*
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Database
import androidx.room.RoomDatabase


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

    @Query("SELECT * FROM youtube_videos ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getVideosPaginated(limit: Int, offset: Int): List<YouTubeVideoEntity>

    @Query("SELECT * FROM youtube_videos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getVideosByQueryPaginated(query: String, limit: Int, offset: Int): List<YouTubeVideoEntity>

    @Upsert
    suspend fun updateVideo(video: YouTubeVideoEntity)

    @Delete
    suspend fun deleteVideo(video: YouTubeVideoEntity)

    @Query("DELETE FROM youtube_videos")
    suspend fun deleteAll()
}


@Database(entities = [YouTubeVideoEntity::class], version = 1)
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