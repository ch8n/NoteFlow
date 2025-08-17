package dev.ch8n.noteflow.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.data.fetchYouTubeVideoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onTranscriptionDownload: (youtubeUrl: String) -> Unit,
    onAiDigest: (youtubeUrl: String) -> Unit,
    defaultYoutubeUrl: String?
) {
    val viewmodel = remember { HomeScreenViewModel() }
    val youTubeVideo by viewmodel.youtubeVideoEntityData.collectAsState()
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
                viewmodel.fetchYouTubeVideo(youtubeUrl)
            }) {
                Text("Fetch Details")
            }
        }

        if (youTubeVideo != null) {
            YouTubeVideoDetail(youTubeVideo!!)
        } else {
            item {
                Box(
                    Modifier
                        .size(200.dp)
                        .background(Color.LightGray, RoundedCornerShape(8))
                )
            }
        }

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

class HomeScreenViewModel : ViewModel() {

    val youtubeVideoEntityData = MutableStateFlow<YouTubeVideoEntity?>(null)

    fun fetchYouTubeVideo(youtubeUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = fetchYouTubeVideoData(youtubeUrl)
            youtubeVideoEntityData.update { result }
        }
    }
}


fun LazyListScope.YouTubeVideoDetail(video: YouTubeVideoEntity) {

    item {
        AsyncImage(
            model = video.thumbnailUrl,
            contentDescription = "Thumbnail",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )
    }

    item {
        Text(text = video.title ?: "No title", style = MaterialTheme.typography.headlineSmall)
    }

    item {
        Text(
            text = video.description ?: "No description",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


