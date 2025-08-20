package dev.ch8n.noteflow.ui.features.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import dev.ch8n.noteflow.MessageUtil
import dev.ch8n.noteflow.data.AppDatabase
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.data.extractVideoId
import dev.ch8n.noteflow.data.fetchYouTubeVideoData
import dev.ch8n.noteflow.ui.features.aiDigest.AiDigestModelBottomSheet
import dev.ch8n.noteflow.ui.features.aiDigest.AiNotesGeneratorViewModel
import dev.ch8n.noteflow.ui.features.transcription.TranscriptionModelBottomSheet
import dev.ch8n.noteflow.ui.features.transcription.TranscriptionViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.text.ifEmpty


@Composable
fun VideoDetailScreen(
    modifier: Modifier = Modifier,
    youTubeVideo: YouTubeVideoEntity,
    viewModel: HomeScreenViewModel,
    onBack: () -> Unit,
    transcriptionViewModel: TranscriptionViewModel,
    aiNotesGeneratorViewModel: AiNotesGeneratorViewModel
) {

    LaunchedEffect(Unit) {
        if (youTubeVideo.videoUrl != null) {
            viewModel.localOrFetchRemoteYouTubeVideo(youTubeVideo.videoUrl)
        } else {
            MessageUtil.showToast("No youtube url found")
            onBack.invoke()
        }
    }

    val youTubeVideo by viewModel.youtubeVideoEntityData.collectAsState()

    if (youTubeVideo != null) {
        VideoDetailContent(
            modifier = modifier,
            youTubeVideo = requireNotNull(youTubeVideo),
            onDeleteYoutubeVideo = { video ->
                viewModel.deleteYoutubeVideo(
                    youTubeVideo = video,
                    onDeleted = onBack
                )
            },
            transcriptionViewModel = transcriptionViewModel,
            aiNotesGeneratorViewModel = aiNotesGeneratorViewModel
        )
    } else {
        Box(
            Modifier
                .padding(36.dp)
                .fillMaxSize()
                .background(Color.LightGray, RoundedCornerShape(16.dp))
        )
    }
}


@Composable
fun VideoDetailContent(
    modifier: Modifier = Modifier,
    youTubeVideo: YouTubeVideoEntity,
    onDeleteYoutubeVideo: (youTubeVideo: YouTubeVideoEntity) -> Unit,
    transcriptionViewModel: TranscriptionViewModel,
    aiNotesGeneratorViewModel: AiNotesGeneratorViewModel,
) {

    var isTranscriptionBottomSheetVisible by remember { mutableStateOf(false) }
    var isAiNotesBottomSheetVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                ) {
                    if (!youTubeVideo.thumbnailUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = youTubeVideo.thumbnailUrl,
                            contentDescription = "Thumbnail",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.LightGray, RoundedCornerShape(8))
                        )
                    }

                    IconButton(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .background(Color.LightGray, CircleShape)
                        ,
                        onClick = {
                            onDeleteYoutubeVideo(youTubeVideo)
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "delete",
                            tint = Color.DarkGray
                        )
                    }
                }
            }

            HorizontalDivider()
        }

        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = youTubeVideo.title ?: "No title",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = youTubeVideo.description ?: "No description",
                    style = MaterialTheme.typography.bodyMedium
                )
                HorizontalDivider()
            }
        }

        stickyHeader {
            Column (
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 8.dp),
            ) {
                OutlinedButton(onClick = {
                    isTranscriptionBottomSheetVisible = !isTranscriptionBottomSheetVisible
                }) {
                    val words = (youTubeVideo.transcription?:"")
                        .split(" ")
                        .count { it.isNotEmpty() }

                    Text("Download Transcription ⤵️ | $words")
                }

                OutlinedButton(onClick = {
                    isAiNotesBottomSheetVisible = !isAiNotesBottomSheetVisible
                }) {
                    Text("Generate AI Notes ✨")
                }
            }
        }

        item {
            MarkdownText(
                youTubeVideo.aiDigest
                    ?.ifEmpty { "### No AI Digest, Click Generate" }
                    ?: "### No AI Digest, Click Generate",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 100.dp),
            )
        }
    }

    if (isTranscriptionBottomSheetVisible) {
        TranscriptionModelBottomSheet(
            isBottomSheetVisible = isTranscriptionBottomSheetVisible,
            setBottomSheetVisibility = { isTranscriptionBottomSheetVisible = it },
            youTubeVideo = youTubeVideo,
            transcriptionViewModel = transcriptionViewModel
        )
    }

    if (isAiNotesBottomSheetVisible) {
        AiDigestModelBottomSheet(
            isBottomSheetVisible = isAiNotesBottomSheetVisible,
            setBottomSheetVisibility = { isAiNotesBottomSheetVisible = it },
            youTubeVideo = youTubeVideo,
            aiNotesGeneratorViewModel = aiNotesGeneratorViewModel
        )
    }
}

class HomeScreenViewModel(appDatabase: AppDatabase) : ViewModel() {

    private val youTubeVideoDao = appDatabase.youtubeVideoDao()

    val youtubeVideoEntityData = MutableStateFlow<YouTubeVideoEntity?>(null)

    fun localOrFetchRemoteYouTubeVideo(youtubeUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val videoId = extractVideoId(youtubeUrl)
                ?: return@launch MessageUtil.showToast("Video id null from $youtubeUrl")
            var videoEntity = youTubeVideoDao.getVideoById(videoId)
            if (videoEntity == null) {
                videoEntity = fetchYouTubeVideoData(youtubeUrl)
                    ?: return@launch MessageUtil.showToast("No Remote video found")
                youTubeVideoDao.updateVideo(videoEntity)
            }
            youtubeVideoEntityData.update { videoEntity }
        }
    }

    fun deleteYoutubeVideo(youTubeVideo: YouTubeVideoEntity, onDeleted: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            youTubeVideoDao.deleteVideo(youTubeVideo)
            withContext(Dispatchers.Main.immediate) {
                onDeleted.invoke()
            }
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


