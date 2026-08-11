package com.mimo.app.ui.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mimo.app.data.AppOpenCount
import com.mimo.app.data.ClosureLog
import com.mimo.app.repository.MimoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

data class StatsUiState(
    val closuresToday: Int = 0,
    val timeGuardedTodayMillis: Long = 0L,
    val recentLogs: List<ClosureLog> = emptyList(),
    val topApps: List<AppOpenCount> = emptyList()
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MimoRepository.getInstance(application)

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        viewModelScope.launch {
            combine(
                repository.observeClosuresToday(startOfDay),
                repository.observeTimeGuardedSince(startOfDay),
                repository.observeRecentLogs(),
                repository.observeTopApps()
            ) { closures, timeGuarded, logs, topApps ->
                StatsUiState(closures, timeGuarded, logs, topApps)
            }.collect { _uiState.value = it }
        }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearLogs() }
    }
}
