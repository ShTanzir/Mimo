package com.mimo.app.ui.appdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mimo.app.data.AppRule
import com.mimo.app.repository.MimoRepository
import com.mimo.app.util.AppInfoProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppDetailUiState(
    val packageName: String = "",
    val appLabel: String = "",
    val enabled: Boolean = true,
    val selectedDelayMillis: Long = 60_000L,
    val selectedPresetLabel: String = "1 minute",
    val customMinutes: String = "",
    val customSeconds: String = "",
    val closeMessage: String = "Time's up! MIMO is closing this app.",
    val allowSnoozeOnce: Boolean = true,
    val vibrateOnWarning: Boolean = true,
    val ruleExists: Boolean = false,
    val saved: Boolean = false
)

class AppDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MimoRepository.getInstance(application)

    private val _uiState = MutableStateFlow(AppDetailUiState())
    val uiState: StateFlow<AppDetailUiState> = _uiState.asStateFlow()

    fun load(packageName: String) {
        viewModelScope.launch {
            val label = AppInfoProvider.getAppLabel(getApplication(), packageName)
            val existing = repository.getRule(packageName)
            _uiState.value = if (existing != null) {
                AppDetailUiState(
                    packageName = packageName,
                    appLabel = label,
                    enabled = existing.enabled,
                    selectedDelayMillis = existing.delayMillis,
                    selectedPresetLabel = existing.delayPresetLabel,
                    closeMessage = existing.closeMessage,
                    allowSnoozeOnce = existing.allowSnoozeOnce,
                    vibrateOnWarning = existing.vibrateOnWarning,
                    ruleExists = true
                )
            } else {
                AppDetailUiState(packageName = packageName, appLabel = label, ruleExists = false)
            }
        }
    }

    fun selectPreset(label: String, millis: Long) {
        _uiState.value = _uiState.value.copy(selectedPresetLabel = label, selectedDelayMillis = millis)
    }

    fun setCustomMinutes(v: String) { _uiState.value = _uiState.value.copy(customMinutes = v) }
    fun setCustomSeconds(v: String) { _uiState.value = _uiState.value.copy(customSeconds = v) }

    fun applyCustomTime() {
        val mins = _uiState.value.customMinutes.toLongOrNull() ?: 0L
        val secs = _uiState.value.customSeconds.toLongOrNull() ?: 0L
        val total = (mins * 60_000L) + (secs * 1000L)
        _uiState.value = _uiState.value.copy(
            selectedDelayMillis = total,
            selectedPresetLabel = "Custom"
        )
    }

    fun setCloseMessage(msg: String) { _uiState.value = _uiState.value.copy(closeMessage = msg) }
    fun setAllowSnooze(v: Boolean) { _uiState.value = _uiState.value.copy(allowSnoozeOnce = v) }
    fun setVibrate(v: Boolean) { _uiState.value = _uiState.value.copy(vibrateOnWarning = v) }

    fun save() {
        val s = _uiState.value
        viewModelScope.launch {
            repository.saveRule(
                AppRule(
                    packageName = s.packageName,
                    appLabel = s.appLabel,
                    enabled = true,
                    delayMillis = s.selectedDelayMillis,
                    delayPresetLabel = s.selectedPresetLabel,
                    closeMessage = s.closeMessage,
                    allowSnoozeOnce = s.allowSnoozeOnce,
                    vibrateOnWarning = s.vibrateOnWarning
                )
            )
            _uiState.value = s.copy(saved = true, ruleExists = true, enabled = true)
        }
    }

    fun deleteRule() {
        viewModelScope.launch {
            repository.deleteRule(_uiState.value.packageName)
            _uiState.value = _uiState.value.copy(ruleExists = false, saved = false)
        }
    }
}
