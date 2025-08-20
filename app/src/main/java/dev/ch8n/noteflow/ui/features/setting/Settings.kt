package dev.ch8n.noteflow.ui.features.setting

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier,
    viewModel: SettingsViewModel
) {
    val modelName by viewModel.modelName.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Open Router Setting",
            style = TextStyle.Default.copy(fontSize = 16.sp)
        )

        Text("Model Name")
        TextField(
            value = modelName,
            onValueChange = viewModel::updateModelName
        )

        Text("API Key")
        TextField(
            value = apiKey,
            onValueChange = viewModel::updateApiKey,
            visualTransformation = PasswordVisualTransformation()
        )
    }
}


class SettingsViewModel(appContext: Context) : ViewModel() {
    companion object {
        private const val PREFS_NAME = "koog_settings"
        private const val KEY_MODEL_NAME = "model_name"
        private const val KEY_API_KEY = "api_key"
    }

    private val prefs = appContext.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val modelName = MutableStateFlow(prefs.getString(KEY_MODEL_NAME, "") ?: "")

    val apiKey = MutableStateFlow(prefs.getString(KEY_API_KEY, "") ?: "")

    fun updateModelName(modelName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            this@SettingsViewModel.modelName.update { modelName }
            prefs.edit { putString(KEY_MODEL_NAME, modelName) }
        }
    }

    fun updateApiKey(apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            this@SettingsViewModel.apiKey.update { apiKey }
            prefs.edit { putString(KEY_API_KEY, apiKey) }
        }
    }
}
