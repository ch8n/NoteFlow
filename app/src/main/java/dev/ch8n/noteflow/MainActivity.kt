package dev.ch8n.noteflow

import android.annotation.SuppressLint
import android.app.ComponentCaller
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.ui.navigation.AppNavDestination
import dev.ch8n.noteflow.ui.navigation.AppNavigation
import dev.ch8n.noteflow.ui.navigation.VideoDetailsScreen
import dev.ch8n.noteflow.ui.navigation.VideoSearchScreen
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


class MainActivity : ComponentActivity() {

    val backStack = mutableStateListOf<AppNavDestination>(VideoSearchScreen)
    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrEmpty()) {
                backStack.add(
                    VideoDetailsScreen(
                        YouTubeVideoEntity.Empty.copy(
                            videoUrl = sharedText
                        )
                    )
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        handleIncomingIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MessageUtil.setContext(this)
        enableEdgeToEdge()
        handleIncomingIntent(intent)
        setContent {
            NoteFlowTheme {
                Scaffold { innerPadding ->
                    Box(
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        AppNavigation(
                            backStack = backStack
                        )
                    }
                }
            }
        }
    }
}