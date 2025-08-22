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
    aiNotesGeneratorViewModel: AiNotesGeneratorViewModel,
    onAiNotesSaved: (notes: String) -> Unit
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
                onAiNotesSaved.invoke(it)
            }
        )
    }
}

@Composable
fun AiNoteGeneratorScreen(
    modifier: Modifier = Modifier,
    youTubeVideo: YouTubeVideoEntity,
    aiNotesGeneratorViewModel: AiNotesGeneratorViewModel,
    onAiNotesSaved: (aiResponse: String) -> Unit
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

    // Initialize the daily limit counter when the screen is first composed
    LaunchedEffect(Unit) {
        aiNotesGeneratorViewModel.initializeDailyLimit(prefs)
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
                            prompt = """
                                # YouTube Transcription Analyzer v2.0 in English

                                ## ANALYSIS INSTRUCTIONS:
                                1. **Content Assessment**: First evaluate transcription quality (1-10) and video type
                                2. **Content Filtering**: Ignore filler words, repetitions, and off-topic tangents
                                3. **Accuracy Priority**: Only include information explicitly mentioned in transcription

                                ## OUTPUT REQUIREMENTS:

                                ### 1. 📊 **Content Overview** (NEW SECTION)
                                - **Video Type**: [Tutorial/Interview/Review/Lecture/Discussion]
                                - **Target Audience**: [Beginner/Intermediate/Expert] 
                                - **Content Quality**: [Transcription quality score 1-5]
                                - **Duration Estimate**: [Based on content depth]

                                ### 2. 📝 **TLDR** 
                                Create a compelling summary that answers: "Why should someone care about this content?"

                                ### 3. 🔗 **Resources & References** 
                                **Format each as:**
                                - **[Category]**: Name/Title → Purpose/Context

                                **Categories:**
                                - 🌐 Websites & Links
                                - 🛠️ Tools & Software  
                                - 📚 Books & Publications
                                - 🎯 Techniques & Methods
                                - 👥 People & Experts
                                - 🏢 Companies & Organizations

                                ### 4. 💡 **Key Insights**
                                Prioritize:
                                - Actionable advice
                                - Counterintuitive findings  
                                - Expert opinions
                                - Data/statistics mentioned
                                - Problem-solution pairs

                                ### 5. 🏷️ **Strategic Tagging**

                                **Content Classification:**
                                - Primary Category: [Single main topic]
                                - Secondary Categories: [2-3 related areas]
                                - Content Format: [How-to/Analysis/Opinion/News/Case Study]

                                **SEO Tags (12-15 tags):**
                                - High-volume keywords (3-4)
                                - Long-tail keywords (4-6)  
                                - Niche-specific terms (4-5)

                                ### 6. 📚 **Learning Article** (800-1200 words)

                                **Structure:**
                                ```
                                **[Compelling Title with Primary Keyword]**

                                **Meta Description**: (155 characters max)

                                **Learning Objectives** (3-4 bullets starting with action verbs)

                                **Prerequisites**: What learners should know beforehand

                                **Content Chapters**:
                                Chapter 1: [Foundation concepts]
                                Chapter 2: [Core methodology] 
                                Chapter 3: [Advanced applications]
                                Chapter 4: [Implementation & next steps]

                                **Practical Exercises**: 2-3 actionable tasks

                                **Success Metrics**: How to measure learning progress

                                **Related Topics**: What to learn next
                                ```

                                ## 🛡️ **ERROR HANDLING:**
                                - If transcription is <200 words: Note "Limited content available"
                                - If highly technical: Include "Requires domain expertise" warning  
                                - If promotional content: Separate facts from marketing claims
                                - If multiple topics: Create subsections for each major theme

                                ## 🎯 **QUALITY BENCHMARKS:**
                                - Every claim must be traceable to transcription
                                - No assumptions beyond what's explicitly stated
                                - Professional tone throughout
                                - Consistent formatting
                                - Logical flow between sections

                                ---
                                **TRANSCRIPTION INPUT:**
                                
                                ${youTubeVideo.transcription}
                                ```
                            """.trimIndent(),
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

    fun initializeDailyLimit(prefs: SharedPreferences) {
        viewModelScope.launch(Dispatchers.IO) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val lastDate = prefs.getString("last_date", null)

            // Reset counter if date has changed
            if (lastDate != today) {
                prefs.edit().apply {
                    putString("last_date", today)
                    putInt("counter", 0)
                    apply()
                }
                aiLimitCount.update { 0 }
            } else {
                // Load existing count for today
                val currentCount = prefs.getInt("counter", 0)
                aiLimitCount.update { currentCount }
            }
        }
    }

    fun generateAiNotes(
        prompt: String,
        youTubeVideoEntity: YouTubeVideoEntity,
        onComplete: () -> Unit
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
        onAiNotesSaved: (aiResponse: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = youTubeVideoEntity.copy(aiDigest = aiResponse)
            youTubeVideoDao.updateVideo(updated)
            withContext(Dispatchers.Main.immediate) {
                MessageUtil.showToast("Saved!")
                onAiNotesSaved.invoke(aiResponse)
            }
        }
    }

    fun updateAIDailyLimit(prefs: SharedPreferences) {
        viewModelScope.launch(Dispatchers.IO) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val lastDate = prefs.getString("last_date", null)

            // Reset counter if date has changed
            if (lastDate != today) {
                prefs.edit().apply {
                    putString("last_date", today)
                    putInt("counter", 0)
                    apply()
                }
                // Update StateFlow to reflect the reset
                aiLimitCount.update { 0 }
            }

            // Get current count and increment
            val currentCount = prefs.getInt("counter", 0)
            val newCount = (currentCount + 1).coerceAtMost(50)

            // Update the counter in SharedPreferences
            prefs.edit().apply {
                putInt("counter", newCount)
                apply()
            }

            // Update StateFlow with the new count
            aiLimitCount.update { newCount }
        }
    }

}