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
    val aiDigest: String?
)


@Dao
interface YouTubeVideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: YouTubeVideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videos: List<YouTubeVideoEntity>)

    @Query("SELECT * FROM youtube_videos WHERE videoId = :id")
    suspend fun getVideoById(id: String): YouTubeVideoEntity?

    @Query("SELECT * FROM youtube_videos ORDER BY title ASC")
    suspend fun getAllVideos(): List<YouTubeVideoEntity>

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