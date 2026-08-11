package com.mimo.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mimo.app.util.Prefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SettingsUiState(
    val masterEnabled: Boolean = true,
    val darkTheme: Boolean = false,
    val pinEnabled: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = Prefs(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(prefs.masterEnabled, prefs.darkTheme, prefs.pinEnabled) { master, dark, pin ->
                SettingsUiState(master, dark, pin)
            }.collect { _uiState.value = it }
        }
    }

    fun setMasterEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setMasterEnabled(enabled) }
    }

    fun setDarkTheme(dark: Boolean) {
        viewModelScope.launch { prefs.setDarkTheme(dark) }
    }

    fun setPin(pin: String?) {
        viewModelScope.launch { prefs.setPin(pin) }
    }
}
