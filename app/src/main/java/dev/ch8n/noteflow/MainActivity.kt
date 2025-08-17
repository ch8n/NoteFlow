package dev.ch8n.noteflow

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.ch8n.noteflow.Screens.*
import dev.ch8n.noteflow.data.DatabaseProvider
import dev.ch8n.noteflow.ui.features.ai_digest.AiNoteGeneratorScreen
import dev.ch8n.noteflow.ui.features.ai_digest.AiNotesGeneratorViewModel
import dev.ch8n.noteflow.ui.features.details.HomeScreenViewModel
import dev.ch8n.noteflow.ui.features.details.VideoDetailScreen
import dev.ch8n.noteflow.ui.features.search.YouTubeVideoListViewModel
import dev.ch8n.noteflow.ui.features.search.YoutubeSearchScreen
import dev.ch8n.noteflow.ui.features.setting.SettingsScreen
import dev.ch8n.noteflow.ui.features.setting.SettingsViewModel
import dev.ch8n.noteflow.ui.features.transcription.TranscriptionScreen
import dev.ch8n.noteflow.ui.features.transcription.TranscriptionViewModel
import dev.ch8n.noteflow.ui.theme.NoteFlowTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


@SuppressLint("StaticFieldLeak")
object MessageUtil {

    private var context: Context? = null
    fun setContext(context: Context) {
        this.context = context.applicationContext
    }

    suspend fun showToast(message: String) = withContext(Dispatchers.Main.immediate) {
        context ?: return@withContext
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

// commit
object AppVideoLinkManager {
    var youtubeUrl = mutableStateOf("")
}

class MainActivity : ComponentActivity() {

    var selectedScreen = mutableStateOf<Screens>(Screens.VideoSearch)
    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrEmpty()) {
                AppVideoLinkManager.youtubeUrl.value = sharedText
                selectedScreen.value = Screens.VideoDetail
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MessageUtil.setContext(this)
        enableEdgeToEdge()
        handleIncomingIntent(intent)
        setContent {
            NoteFlowTheme {
                val context = LocalContext.current
                val appDatabase = remember(context) { DatabaseProvider.getDatabase(context) }
                val settingsViewModel = remember { SettingsViewModel(application) }

                BackHandler {
                    selectedScreen.value = Screens.VideoSearch
                }

                Scaffold() { innerPadding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(onClick = {
                                selectedScreen.value = Screens.VideoSearch
                            }) {
                                Text("Home 🏠")
                            }
                            OutlinedButton(onClick = {
                                selectedScreen.value = Screens.Settings
                            }) {
                                Text("Settings ⚙️")
                            }
                        }

                        when (selectedScreen.value) {
                            Settings -> {
                                SettingsScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    viewModel = settingsViewModel
                                )
                            }

                            is VideoDetail -> {
                                VideoDetailScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    onTranscriptionDownload = {
                                        selectedScreen.value = Transcription(it)
                                    },
                                    onAiDigest = {
                                        selectedScreen.value = AiNoteGenerator(it)
                                    },
                                    defaultYoutubeUrl = AppVideoLinkManager.youtubeUrl.value,
                                    viewModel = remember { HomeScreenViewModel(appDatabase) }
                                )
                            }

                            is Transcription -> {
                                TranscriptionScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    youtubeUrl = (selectedScreen.value as Transcription).youtubeUrl,
                                    transcriptionViewModel = remember {
                                        TranscriptionViewModel(
                                            appDatabase
                                        )
                                    }
                                )
                            }

                            is AiNoteGenerator -> {
                                AiNoteGeneratorScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    youtubeUrl = (selectedScreen.value as AiNoteGenerator).youtubeUrl,
                                    aiNotesGeneratorViewModel = remember {
                                        AiNotesGeneratorViewModel(
                                            appDatabase,
                                            settingsViewModel
                                        )
                                    },
                                )
                            }

                            VideoSearch -> {
                                YoutubeSearchScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    youtubeVideoListViewModel = remember {
                                        YouTubeVideoListViewModel(appDatabase)
                                    },
                                    onVideoDetailsClicked = { video ->
                                        AppVideoLinkManager.youtubeUrl.value = video.videoUrl
                                            ?: return@YoutubeSearchScreen runBlocking {
                                                MessageUtil.showToast(
                                                    "video.videoUrl is null"
                                                )
                                            }
                                        selectedScreen.value = Screens.VideoDetail
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class Screens {
    object Settings : Screens()
    object VideoDetail : Screens()
    object VideoSearch : Screens()
    data class Transcription(val youtubeUrl: String) : Screens()
    data class AiNoteGenerator(val youtubeUrl: String) : Screens()
}