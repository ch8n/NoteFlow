package dev.ch8n.noteflow.ui.features.aiDigest

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ch8n.noteflow.MessageUtil
import dev.ch8n.noteflow.data.AppDatabase
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.data.initKoogAiAgent
import dev.ch8n.noteflow.ui.features.setting.SettingsViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiDigestModelBottomSheet(
    modifier: Modifier = Modifier,
    isBottomSheetVisible: Boolean,
    setBottomSheetVisibility: (Boolean) -> Unit,
    youTubeVideo: YouTubeVideoEntity,
    aiNotesGeneratorViewModel: AiNotesGeneratorViewModel
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
        AiNoteGeneratorScreen(
            modifier = modifier,
            youTubeVideo = youTubeVideo,
            aiNotesGeneratorViewModel = aiNotesGeneratorViewModel,
            onAiNotesSaved = {
                setBottomSheetVisibility.invoke(false)
            }
        )
    }
}

@Composable
fun AiNoteGeneratorScreen(
    modifier: Modifier = Modifier,
    youTubeVideo: YouTubeVideoEntity,
    aiNotesGeneratorViewModel: AiNotesGeneratorViewModel,
    onAiNotesSaved: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences(
            "daily_counter_prefs",
            Context.MODE_PRIVATE
        )
    }
    val aiResponse by aiNotesGeneratorViewModel.aiResponse.collectAsState()
    val aiLimitCount by aiNotesGeneratorViewModel.aiLimitCount.collectAsState()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("Ai Digest", fontSize = 18.sp)
        }

        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                MarkdownText(
                    aiResponse.ifEmpty {
                        youTubeVideo.aiDigest
                            ?.ifEmpty { "### No AI Digest, Click Generate" }
                            ?: "### No AI Digest, Click Generate"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 100.dp),
                )
            }
        }

        stickyHeader {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                OutlinedButton(
                    onClick = {
                        aiNotesGeneratorViewModel.generateAiNotes(
                            prompt = "You are a helpful assistant. Convert the following Youtube Video Transcription to TLDR",
                            youTubeVideoEntity = youTubeVideo,
                            onComplete = {
                                aiNotesGeneratorViewModel.updateAIDailyLimit(prefs)
                            }
                        )
                    }
                ) {
                    Text("Regenerate ✨ ${aiLimitCount}/50")
                }

                OutlinedButton(
                    onClick = {
                        aiNotesGeneratorViewModel.saveAiDigest(
                            youTubeVideoEntity = youTubeVideo,
                            aiResponse = aiResponse,
                            onAiNotesSaved = onAiNotesSaved
                        )
                    }
                ) {
                    Text("Save 💾")
                }
            }
        }
    }
}

class AiNotesGeneratorViewModel(
    appDatabase: AppDatabase,
    private val settingsViewModel: SettingsViewModel,
) : ViewModel() {

    private val youTubeVideoDao = appDatabase.youtubeVideoDao()
    val aiResponse = MutableStateFlow("")
    val aiLimitCount = MutableStateFlow(0)

    fun generateAiNotes(
        prompt: String,
        youTubeVideoEntity: YouTubeVideoEntity,
        onComplete : () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val transcription = youTubeVideoEntity.transcription
                    ?: return@launch MessageUtil.showToast("Download Transcription first!")

                val aiAgent = initKoogAiAgent(
                    apiKey = settingsViewModel.apiKey.value,
                    modelName = settingsViewModel.modelName.value,
                    systemPrompt = prompt
                )
                val response = aiAgent.run(transcription)
                aiResponse.update { response }
                withContext(Dispatchers.Main.immediate) {
                    onComplete.invoke()
                }
            } catch (error: Exception) {
                Log.e("AiNotesGeneratorViewModel", "generateAiNotes: ", error)
                aiResponse.update { "Error: ${error.message}" }
                withContext(Dispatchers.Main.immediate) {
                    onComplete.invoke()
                }
            }
        }
    }

    fun saveAiDigest(
        youTubeVideoEntity: YouTubeVideoEntity,
        aiResponse: String,
        onAiNotesSaved: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = youTubeVideoEntity.copy(aiDigest = aiResponse)
            youTubeVideoDao.updateVideo(updated)
            withContext(Dispatchers.Main.immediate) {
                onAiNotesSaved.invoke()
            }
        }
    }

    fun updateAIDailyLimit(prefs: SharedPreferences) {
        viewModelScope.launch(Dispatchers.IO) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val lastDate = prefs.getString("last_date", null)
            prefs.edit {
                // Reset if date changed
                if (lastDate != today) {
                    putString("last_date", today)
                    putInt("counter", 0)
                }
                // Increment counter
                val currentCount = prefs.getInt("counter", 0)
                val newCount = (currentCount + 1).coerceAtMost(50)
                putInt("counter", newCount)
            }

            val updatedCount = prefs.getInt("counter", 0)
            aiLimitCount.update { updatedCount }
        }
    }
}