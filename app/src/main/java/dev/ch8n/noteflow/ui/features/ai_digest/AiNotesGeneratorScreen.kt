package dev.ch8n.noteflow.ui.features.ai_digest

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ch8n.noteflow.MessageUtil
import dev.ch8n.noteflow.data.AppDatabase
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.data.extractVideoId
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

@Composable
fun AiNoteGeneratorScreen(
    modifier: Modifier = Modifier,
    youtubeUrl: String,
    aiNotesGeneratorViewModel: AiNotesGeneratorViewModel,
) {
    val context = LocalContext.current
    val prefs = remember(context) {  context.getSharedPreferences("daily_counter_prefs", Context.MODE_PRIVATE) }
    var youTubeVideoEntity by remember { mutableStateOf<YouTubeVideoEntity?>(null) }
    val aiRespone by aiNotesGeneratorViewModel.aiResponse.collectAsState()
    val aiLimitCount by aiNotesGeneratorViewModel.aiLimitCount.collectAsState()

    LaunchedEffect(youtubeUrl) {
        aiNotesGeneratorViewModel.observeSharePrefs(prefs)
        launch(Dispatchers.IO) {
            val videoId = aiNotesGeneratorViewModel.getVideoId(youtubeUrl) ?: return@launch
            youTubeVideoEntity = aiNotesGeneratorViewModel.getTranscription(videoId)
            val aiRespone = youTubeVideoEntity?.aiDigest ?: return@launch
            aiNotesGeneratorViewModel.updateAiResponse(aiRespone)
        }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("Ai Digest", fontSize = 18.sp)
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedButton(
                    onClick = {
                        youTubeVideoEntity ?: return@OutlinedButton
                        aiNotesGeneratorViewModel.updateAIDailyLimit(context)
                        aiNotesGeneratorViewModel.generateAiNotes(
                            prompt = "You are a helpful assistant. Convert the following Youtube Video Transcription to TLDR",
                            youTubeVideoEntity = youTubeVideoEntity!!
                        )
                    }
                ) {
                    Text("Regenerate ✨ ${aiLimitCount}/50")
                }

                OutlinedButton(
                    onClick = {
                        youTubeVideoEntity ?: return@OutlinedButton
                        aiNotesGeneratorViewModel.saveAiDigest(
                            youTubeVideoEntity!!,
                            aiRespone
                        )
                    }
                ) {
                    Text("Save 💾")
                }
            }
        }

        item {
            MarkdownText(
                aiRespone.ifEmpty { "### No AI Digest, Click Generate" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            )
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
    fun getVideoId(youtubeUrl: String): String? {
        return extractVideoId(youtubeUrl)
    }

    fun updateAiResponse(aiResponse: String) {
        this.aiResponse.update { aiResponse }
    }

    suspend fun getTranscription(videoId: String): YouTubeVideoEntity? {
        return youTubeVideoDao.getVideoById(videoId)
    }

    fun generateAiNotes(prompt: String, youTubeVideoEntity: YouTubeVideoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val transcription = youTubeVideoEntity.transcription
                val aiAgent = initKoogAiAgent(
                    apiKey = settingsViewModel.apiKey.value,
                    modelName = settingsViewModel.modelName.value,
                    systemPrompt = prompt
                )
                if (transcription != null) {
                    val response = aiAgent.run(transcription)
                    updateAiResponse(response)
                }
            } catch (error: Exception) {
                Log.e("AiNotesGeneratorViewModel", "generateAiNotes: ", error)
                MessageUtil.showToast("Error: ${error.message}")
            }
        }
    }

    fun saveAiDigest(youTubeVideoEntity: YouTubeVideoEntity, aiResponse: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = youTubeVideoEntity.copy(aiDigest = aiResponse)
            youTubeVideoDao.updateVideo(updated)
        }
    }


    fun observeSharePrefs(prefs: SharedPreferences) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPrefs, key ->
            if (key == "counter") {
                val updatedCount = sharedPrefs.getInt("counter", 0)
                aiLimitCount.update { updatedCount }
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        //TODO prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun updateAIDailyLimit(context: Context) {
        val prefs = context.getSharedPreferences("daily_counter_prefs", Context.MODE_PRIVATE)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastDate = prefs.getString("last_date", null)
        prefs.edit {
            // Reset if date changed
            if (lastDate != today) {
                putString("last_date", today)
                putInt("counter", 0)
            }

            // Increment counter
            val newCount = (prefs.getInt("counter", 0) + 1).coerceAtMost(50)
            putInt("counter", newCount)
        }
    }
}