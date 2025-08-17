package dev.ch8n.noteflow.ui.features.transcription

import android.os.Handler
import android.os.Looper
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ch8n.noteflow.data.AppDatabase
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.data.extractVideoId
import dev.ch8n.noteflow.data.httpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun TranscriptionScreen(
    modifier: Modifier = Modifier,
    youtubeUrl: String,
    transcriptionViewModel: TranscriptionViewModel
) {
    LazyColumn(modifier = modifier) {
        TranscriptDownloaderContent(
            youtubeUrl,
            transcriptionViewModel = transcriptionViewModel,
        )
    }
}


class TranscriptionViewModel(appDatabase: AppDatabase) : ViewModel() {
    private val youtubeVideoDao = appDatabase.youtubeVideoDao()
    fun saveTranscription(youtubeUrl: String, transcription: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val youtubeId = extractVideoId(youtubeUrl)
            if (youtubeId != null) {
                val youTubeVideoEntity = youtubeVideoDao.getVideoById(youtubeId) ?: YouTubeVideoEntity(
                    videoId = youtubeId,
                    videoUrl = youtubeUrl,
                    title = null,
                    description = null,
                    thumbnailUrl = null,
                    transcription = null,
                    aiDigest = null
                )
                val updatedYouTubeVideoEntity = youTubeVideoEntity.copy(
                    transcription = transcription
                )
                youtubeVideoDao.updateVideo(updatedYouTubeVideoEntity)
            }
        }
    }

    fun getTranscription(youtubeUrl: String): String? {
        return runBlocking {
            val youtubeId = extractVideoId(youtubeUrl) ?: return@runBlocking null
            val youTubeVideoEntity =
                youtubeVideoDao.getVideoById(youtubeId) ?: return@runBlocking null
            youTubeVideoEntity.transcription
        }
    }
}

fun LazyListScope.TranscriptDownloaderContent(
    youtubeUrl: String,
    transcriptionViewModel: TranscriptionViewModel,
) {

    item {
        var transcriptText by remember { mutableStateOf("Transcript will appear here...") }
        val context = LocalContext.current

        val webView = remember {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.userAgentString = settings.userAgentString + " AndroidApp"

                settings.mediaPlaybackRequiresUserGesture = true

                webChromeClient = object : WebChromeClient() {
                    override fun onPermissionRequest(request: PermissionRequest) {
                        // Block all permissions including camera
                        request.deny()
                    }
                }

                setDownloadListener { url, _, _, _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        if (url.startsWith("data:")) {
                            val encodedContent = url.substringAfter(",")
                            if (encodedContent.isNotEmpty()) {
                                val decodedText = URLDecoder.decode(
                                    encodedContent,
                                    StandardCharsets.UTF_8.toString()
                                )
                                withContext(Dispatchers.Main) {
                                    transcriptText = decodedText
                                    transcriptionViewModel.saveTranscription(
                                        youtubeUrl,
                                        decodedText
                                    )
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    transcriptText = "Invalid base64 data"
                                }
                            }
                        } else {
                            // Use OkHttp for http/https
                            val client = httpClient
                            val request = Request.Builder().url(url).build()
                            try {
                                val response = client.newCall(request).execute()
                                val body = response.body?.string()
                                withContext(Dispatchers.Main) {
                                    transcriptText = body ?: "Empty response"
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    transcriptText = "Error: ${e.localizedMessage}"
                                }
                            }
                        }
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (view == null) return
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1000)
                            observeDownloadElementVisibility(view)
                                .collect { isVisible ->
                                    if (isVisible) {
                                        view.evaluateJavascript(
                                            "document.getElementById('download').click();"
                                        ) {
                                            Toast.makeText(
                                                view.context,
                                                "Download started $it",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                        }
                    }
                }
            }
        }


        LaunchedEffect(youtubeUrl) {
            if (youtubeUrl.isNotEmpty()) {
                launch(Dispatchers.IO) {
                    val transcript = transcriptionViewModel.getTranscription(youtubeUrl)
                    if (transcript != null) {
                        transcriptText = transcript
                        return@launch
                    } else {
                        withContext(Dispatchers.Main.immediate) {
                            val tactiqUrl =
                                "https://tactiq.io/tools/run/youtube_transcript?yt=$youtubeUrl"
                            webView.loadUrl(tactiqUrl)
                        }
                    }
                }
            }
        }

        AndroidView(
            factory = { webView },
            modifier = Modifier.height(0.5.dp)
        )

        Text(
            text = transcriptText,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

fun observeDownloadElementVisibility(webView: WebView): Flow<Boolean> = callbackFlow {
    val handler = Handler(Looper.getMainLooper())

    val checkRunnable = object : Runnable {
        override fun run() {
            webView.evaluateJavascript(
                """
                (function() {
                    var elem = document.getElementById('download');
                    return elem !== null;
                })();
                """.trimIndent()
            ) { result ->
                if (result.equals("true", ignoreCase = true)) {
                    trySend(true).isSuccess
                    close() // Stop observing
                } else {
                    handler.postDelayed(this, 1000)
                }
            }
        }
    }

    handler.post(checkRunnable)

    awaitClose {
        handler.removeCallbacksAndMessages(null)
    }
}
