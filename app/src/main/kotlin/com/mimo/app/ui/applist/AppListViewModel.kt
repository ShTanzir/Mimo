package com.mimo.app.ui.applist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mimo.app.data.AppRule
import com.mimo.app.repository.MimoRepository
import com.mimo.app.util.AppInfoProvider
import com.mimo.app.util.InstalledApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppListUiState(
    val isLoading: Boolean = true,
    val installedApps: List<InstalledApp> = emptyList(),
    val rules: Map<String, AppRule> = emptyMap(),
    val query: String = "",
    val showOnlyGuarded: Boolean = false,
    val showSystemApps: Boolean = false
)

class AppListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MimoRepository.getInstance(application)

    private val _uiState = MutableStateFlow(AppListUiState())
    val uiState: StateFlow<AppListUiState> = _uiState.asStateFlow()

    init {
        loadApps(false)
        viewModelScope.launch {
            repository.observeRules().collect { rules ->
                _uiState.value = _uiState.value.copy(rules = rules.associateBy { it.packageName })
            }
        }
    }

    fun loadApps(includeSystemApps: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val apps = AppInfoProvider.getLaunchableApps(getApplication(), includeSystemApps)
            _uiState.value = _uiState.value.copy(
                installedApps = apps,
                isLoading = false,
                showSystemApps = includeSystemApps
            )
        }
    }

    fun setQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun toggleShowOnlyGuarded() {
        _uiState.value = _uiState.value.copy(showOnlyGuarded = !_uiState.value.showOnlyGuarded)
    }

    fun setRuleEnabled(packageName: String, enabled: Boolean) {
        viewModelScope.launch { repository.setEnabled(packageName, enabled) }
    }

    fun removeRule(packageName: String) {
        viewModelScope.launch { repository.deleteRule(packageName) }
    }

    fun filteredApps(): List<InstalledApp> {
        val state = _uiState.value
        return state.installedApps.filter { app ->
            val matchesQuery = state.query.isBlank() ||
                app.label.contains(state.query, ignoreCase = true)
            val matchesGuarded = !state.showOnlyGuarded || state.rules.containsKey(app.packageName)
            matchesQuery && matchesGuarded
        }
    }
}
