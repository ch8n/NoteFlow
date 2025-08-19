package dev.ch8n.noteflow.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dev.ch8n.noteflow.data.DatabaseProvider
import dev.ch8n.noteflow.data.YouTubeVideoEntity
import dev.ch8n.noteflow.ui.features.details.HomeScreenViewModel
import dev.ch8n.noteflow.ui.features.details.VideoDetailScreen
import dev.ch8n.noteflow.ui.features.search.YouTubeVideoListViewModel
import dev.ch8n.noteflow.ui.features.search.YoutubeSearchScreen
import dev.ch8n.noteflow.ui.features.setting.SettingsScreen
import dev.ch8n.noteflow.ui.features.setting.SettingsViewModel

sealed class AppNavDestination

data object VideoSearchScreen : AppNavDestination()
data object AppSettingScreen : AppNavDestination()
data class VideoDetailsScreen(val video: YouTubeVideoEntity) : AppNavDestination()

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<AppNavDestination>(VideoSearchScreen) }
    val context = LocalContext.current
    val appDatabase = remember(context) { DatabaseProvider.getDatabase(context) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { navDestination ->
            when (navDestination) {
                is VideoSearchScreen -> NavEntry(navDestination) {
                    YoutubeSearchScreen(
                        navigateToVideDetails = { video ->
                            backStack.add(VideoDetailsScreen(video))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        youtubeVideoListViewModel = remember { YouTubeVideoListViewModel(appDatabase) }
                    )
                }

                is VideoDetailsScreen -> NavEntry(navDestination) {
                    VideoDetailScreen(
                        modifier = Modifier.fillMaxSize(),
                        onTranscriptionDownload = {

                        },
                        onAiDigest = {

                        },
                        defaultYoutubeUrl = navDestination.video.videoUrl,
                        viewModel = remember { HomeScreenViewModel(appDatabase) }
                    )
                }

                is AppSettingScreen -> NavEntry(navDestination) {
                    SettingsScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = remember {
                            SettingsViewModel(context.applicationContext)
                        }
                    )
                }
            }
        }
    )
}
