package dev.ch8n.noteflow.ui.features.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import dev.ch8n.noteflow.MessageUtil
import dev.ch8n.noteflow.data.AppDatabase
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.data.extractVideoId
import dev.ch8n.noteflow.data.fetchYouTubeVideoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@Composable
fun VideoDetailScreen(
    modifier: Modifier = Modifier,
    onTranscriptionDownload: (youtubeUrl: String) -> Unit,
    onAiDigest: (youtubeUrl: String) -> Unit,
    defaultYoutubeUrl: String?,
    viewModel: HomeScreenViewModel
) {

    LaunchedEffect(Unit) {
        viewModel.getLocalYoutubeEntity(defaultYoutubeUrl ?: "")
    }

    VideoDetailContent(
        modifier = modifier,
        viewModel = viewModel,
        onTranscriptionDownload = onTranscriptionDownload,
        onAiDigest = onAiDigest,
        defaultYoutubeUrl = defaultYoutubeUrl
    )
}



    @Composable
fun VideoDetailContent(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel,
    onTranscriptionDownload: (youtubeUrl: String) -> Unit,
    onAiDigest: (youtubeUrl: String) -> Unit,
    defaultYoutubeUrl: String?
) {
    val youTubeVideo by viewModel.youtubeVideoEntityData.collectAsState()
    var youtubeUrl by remember { mutableStateOf(defaultYoutubeUrl ?: "") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            OutlinedTextField(
                value = youtubeUrl,
                onValueChange = {
                    youtubeUrl = it
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedButton(onClick = {
                viewModel.fetchRemoteYouTubeVideo(youtubeUrl)
            }) {
                Text("Fetch Details")
            }
        }

        YouTubeVideoDetail(
            youTubeVideo,
            onVideClicked = { video ->
                // do nothing
            }
        )

        item {
            OutlinedButton(onClick = {
                onTranscriptionDownload.invoke(youtubeUrl)
            }) {
                Text("Download Transcription")
            }

            OutlinedButton(onClick = {
                onAiDigest.invoke(youtubeUrl)
            }) {
                Text("AI Digest")
            }
        }
    }
}

class HomeScreenViewModel(appDatabase: AppDatabase) : ViewModel() {

    private val youTubeVideoDao = appDatabase.youtubeVideoDao()

    val youtubeVideoEntityData = MutableStateFlow<YouTubeVideoEntity?>(null)

    fun getLocalYoutubeEntity(youtubeUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val videoId = extractVideoId(youtubeUrl)
                ?: return@launch MessageUtil.showToast("Video id null from $youtubeUrl")
            val videoEntity = youTubeVideoDao.getVideoById(videoId)
            youtubeVideoEntityData.update { videoEntity }
        }
    }

    fun fetchRemoteYouTubeVideo(youtubeUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = fetchYouTubeVideoData(youtubeUrl)
            youtubeVideoEntityData.update { result }
            result ?: return@launch MessageUtil.showToast("fetch result is not saved due to null")
            saveYouTubeVideo(result)
        }
    }

    fun saveYouTubeVideo(video: YouTubeVideoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            youTubeVideoDao.updateVideo(video)
        }
    }
}


fun LazyListScope.YouTubeVideoDetail(
    video: YouTubeVideoEntity?,
    onVideClicked: (video: YouTubeVideoEntity) -> Unit
) {

    item {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    video ?: return@clickable
                    onVideClicked.invoke(video)
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .aspectRatio(16 / 9f)
            ) {
                if (video?.thumbnailUrl != null) {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = "Thumbnail",
                        modifier = Modifier
                            .fillMaxSize()
                        //.aspectRatio(16 / 9f)
                    )
                } else {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.LightGray, RoundedCornerShape(8))
                    )
                }
            }


            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = video?.title ?: "No title",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = video?.description ?: "No description",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.size(8.dp))

                HorizontalDivider()
            }
        }
    }
}


