package dev.ch8n.noteflow.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup


val httpClient = OkHttpClient()

suspend fun fetchYouTubeVideoData(url: String): YouTubeVideoEntity? = withContext(Dispatchers.IO) {
    val videoId = extractVideoId(url) ?: return@withContext null

    val metadataDeferred = async { fetchMetadata(videoId) }
    val thumbnailUrl = getAvailableThumbnailUrl(videoId)

    val (title, description) = metadataDeferred.await()

    return@withContext YouTubeVideoEntity(
        videoId = videoId,
        videoUrl = url,
        title = title,
        description = description,
        thumbnailUrl = thumbnailUrl,
        transcription = null,
        aiDigest = null,
        createdAt = System.currentTimeMillis()
    )
}

fun extractVideoId(url: String): String? {
    val patterns = listOf(
        "youtu\\.be/([\\w-]{11})",
        "youtube\\.com/watch\\?v=([\\w-]{11})",
        "youtube\\.com/shorts/([\\w-]{11})",
        "youtube\\.com/live/([\\w-]{11})",
        "youtube\\.com/embed/([\\w-]{11})"
    )

    for (pattern in patterns) {
        val regex = Regex(pattern)
        val match = regex.find(url)
        if (match != null) return match.groupValues[1]
    }
    return null
}

suspend fun fetchMetadata(videoId: String): Pair<String?, String?> = withContext(Dispatchers.IO) {
    val url = "https://www.youtube.com/watch?v=$videoId"
    val request = Request.Builder().url(url).header("User-Agent", "Mozilla/5.0").build()

    httpClient.newCall(request).execute().use { response ->
        val html = response.body?.string() ?: return@withContext null to null
        val doc = Jsoup.parse(html)
        val title = doc.select("meta[property=og:title]").attr("content")
        val description = doc.select("meta[property=og:description]").attr("content")
        return@withContext title to description
    }
}

fun getAvailableThumbnailUrl(videoId: String): String {
    val resolutions = listOf("maxresdefault", "sddefault", "hqdefault", "mqdefault", "default")
    for (res in resolutions) {
        val url = "https://img.youtube.com/vi/$videoId/$res.jpg"
        if (urlExists(url)) return url
    }
    return "https://img.youtube.com/vi/$videoId/default.jpg"
}

fun urlExists(url: String): Boolean {
    val request = Request.Builder().url(url).head().build()
    httpClient.newCall(request).execute().use { response ->
        return response.isSuccessful
    }
}