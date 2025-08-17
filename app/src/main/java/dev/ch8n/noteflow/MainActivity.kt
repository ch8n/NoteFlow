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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.ch8n.noteflow.Screens.*
import dev.ch8n.noteflow.data.DatabaseProvider
import dev.ch8n.noteflow.ui.features.ai_digest.AiNoteGeneratorScreen
import dev.ch8n.noteflow.ui.features.ai_digest.AiNotesGeneratorViewModel
import dev.ch8n.noteflow.ui.features.home.HomeScreen
import dev.ch8n.noteflow.ui.features.setting.SettingsScreen
import dev.ch8n.noteflow.ui.features.setting.SettingsViewModel
import dev.ch8n.noteflow.ui.features.transcription.TranscriptionScreen
import dev.ch8n.noteflow.ui.features.transcription.TranscriptionViewModel
import dev.ch8n.noteflow.ui.theme.NoteFlowTheme
import kotlinx.coroutines.Dispatchers
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

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrEmpty()) {
                AppVideoLinkManager.youtubeUrl.value = sharedText
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
                val intentSharedContent = AppVideoLinkManager.youtubeUrl.value
                val appDatabase = remember(context) { DatabaseProvider.getDatabase(context) }
                val settingsViewModel = remember { SettingsViewModel(application) }
                var selectedScreen by remember { mutableStateOf<Screens>(Screens.Home) }

                BackHandler {
                    selectedScreen = Screens.Home
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
                                selectedScreen = Screens.Home
                            }) {
                                Text("Home 🏠")
                            }
                            OutlinedButton(onClick = {
                                selectedScreen = Screens.Settings
                            }) {
                                Text("Settings ⚙️")
                            }
                        }

                        when (selectedScreen) {
                            Screens.Settings -> {
                                SettingsScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    viewModel = settingsViewModel
                                )
                            }

                            is Screens.Home -> {
                                HomeScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    onTranscriptionDownload = {
                                        selectedScreen = Transcription(it)
                                    },
                                    onAiDigest = {
                                        selectedScreen = Screens.AiNoteGenerator(it)
                                    },
                                    defaultYoutubeUrl = AppVideoLinkManager.youtubeUrl.value
                                )
                            }

                            is Screens.Transcription -> {
                                TranscriptionScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    youtubeUrl = (selectedScreen as Screens.Transcription).youtubeUrl,
                                    transcriptionViewModel = remember {
                                        TranscriptionViewModel(
                                            appDatabase
                                        )
                                    }
                                )
                            }

                            is Screens.AiNoteGenerator -> {
                                AiNoteGeneratorScreen(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    youtubeUrl = (selectedScreen as Screens.AiNoteGenerator).youtubeUrl,
                                    aiNotesGeneratorViewModel = remember {
                                        AiNotesGeneratorViewModel(
                                            appDatabase,
                                            settingsViewModel
                                        )
                                    },
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
    object Home : Screens()
    data class Transcription(val youtubeUrl: String) : Screens()

    data class AiNoteGenerator(val youtubeUrl: String) : Screens()
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NoteFlowTheme {
        Greeting("Android")
    }
}