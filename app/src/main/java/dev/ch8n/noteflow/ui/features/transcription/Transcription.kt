package dev.ch8n.noteflow.ui.features.transcription

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ch8n.noteflow.MessageUtil
import dev.ch8n.noteflow.data.AppDatabase
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.data.httpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun TranscriptionScreen(
    modifier: Modifier = Modifier,
    youTubeVideo: YouTubeVideoEntity,
    transcriptionViewModel: TranscriptionViewModel
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TranscriptDownloaderContent(
            youTubeVideo = youTubeVideo,
            refreshTranscriptState = transcriptionViewModel.refreshTranscript,
            onTranscriptionDownload = { transcript, errorMessage ->
                transcriptionViewModel.saveTranscription(youTubeVideo, transcript, errorMessage)
            }
        )

        stickyHeader {
            OutlinedButton(onClick = {
                transcriptionViewModel.getOrFetchTranscript(youTubeVideo)
            }) {
                Text("Download Transcription ⤵️")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranscriptionModelBottomSheet(
    modifier: Modifier = Modifier,
    isBottomSheetVisible: Boolean,
    setBottomSheetVisibility: (Boolean) -> Unit,
    youTubeVideo: YouTubeVideoEntity,
    transcriptionViewModel: TranscriptionViewModel,
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(isBottomSheetVisible) {
        if (isBottomSheetVisible) {
            scope.launch {
                sheetState.expand()
            }
        } else {
            scope.launch {
                sheetState.hide()
            }
        }
    }

    BackHandler {
        setBottomSheetVisibility.invoke(false)
    }

    ModalBottomSheet(
        onDismissRequest = {
            setBottomSheetVisibility.invoke(false)
        },
        sheetState = sheetState
    ) {
        TranscriptionScreen(
            modifier = modifier,
            youTubeVideo = youTubeVideo,
            transcriptionViewModel = transcriptionViewModel
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
fun createTranscriptionWebView(
    context: Context,
    onTranscriptionDownload: (transcript: String, errorMessage: String) -> Unit,
): WebView {
    return WebView(context).apply {
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
                            MessageUtil.showToast("Download Complete!")
                            onTranscriptionDownload.invoke(decodedText, "")
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            MessageUtil.showToast("Download Error!")
                            onTranscriptionDownload.invoke("", "Invalid base64 data")
                        }
                    }
                } else {
                    val client = httpClient
                    val request = Request.Builder().url(url).build()
                    try {
                        val response = client.newCall(request).execute()
                        val body = response.body?.string()
                        withContext(Dispatchers.Main) {
                            if (body.isNullOrEmpty()) {
                                MessageUtil.showToast("Download Error!")
                                onTranscriptionDownload.invoke("", "Empty Response")
                            } else {
                                MessageUtil.showToast("Download Complete!")
                                onTranscriptionDownload.invoke(body, "")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            onTranscriptionDownload.invoke("", "Error: ${e.localizedMessage}")
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
                                        "Loading started",
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


class TranscriptionViewModel(appDatabase: AppDatabase) : ViewModel() {
    private val youtubeVideoDao = appDatabase.youtubeVideoDao()

    val transcriptText = MutableStateFlow("Transcript will appear here...")

    @OptIn(ExperimentalUuidApi::class)
    val refreshTranscript = MutableStateFlow<String>(Uuid.random().toString())

    @OptIn(ExperimentalUuidApi::class)
    fun getOrFetchTranscript(youTubeVideoEntity: YouTubeVideoEntity) {
        val transcription = youTubeVideoEntity.transcription
        if (transcription == null) {
            refreshTranscript.update { Uuid.random().toString() }
        } else {
            transcriptText.update { transcription }
        }
    }

    fun saveTranscription(
        youTubeVideo: YouTubeVideoEntity,
        transcription: String,
        errorMessage: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (transcription.isNotEmpty()) {
                val updatedYouTubeVideoEntity = youTubeVideo.copy(transcription = transcription)
                youtubeVideoDao.updateVideo(updatedYouTubeVideoEntity)
                MessageUtil.showToast("Transcription saved!")
            }
            transcriptText.update { transcription.ifEmpty { errorMessage.ifEmpty { "Something went wrong!" } } }
        }
    }
}

fun LazyListScope.TranscriptDownloaderContent(
    youTubeVideo: YouTubeVideoEntity,
    onTranscriptionDownload: (transcript: String, errorMessage: String) -> Unit,
    refreshTranscriptState: StateFlow<String>,
) {

    item {

        val transcriptionRefresh by refreshTranscriptState.collectAsState()

        val context = LocalContext.current

        val transcriptionWebView = remember {
            createTranscriptionWebView(
                context = context,
                onTranscriptionDownload = onTranscriptionDownload
            )
        }

        LaunchedEffect(transcriptionRefresh) {
            if (youTubeVideo.transcription.isNullOrEmpty()) {
                val tactiqUrl =
                    "https://tactiq.io/tools/run/youtube_transcript?yt=${youTubeVideo.videoUrl}"
                transcriptionWebView.loadUrl(tactiqUrl)
            }
        }

        var isExpanded by remember { mutableStateOf(false) }

        val collapseModifier = Modifier
            .fillMaxWidth()
            .height(55.dp)

        val expandedModifier = Modifier
            .fillMaxWidth()
            .height(200.dp)

        Row(
            modifier = Modifier.then(
                if (isExpanded) expandedModifier else collapseModifier
            )
        ) {
            AndroidView(
                factory = { transcriptionWebView },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.background
                    )
            )
            IconButton(onClick = {
                isExpanded = !isExpanded
            }) {
                Icon(
                    if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse"
                )
            }
        }

        Text(
            text = youTubeVideo.transcription ?: "Transcript will appear here...",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
                .verticalScroll(rememberScrollState())
        )

        Spacer(Modifier.size(100.dp))
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
