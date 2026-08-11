package com.mimo.app.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mimo.app.ui.components.GlassCard
import com.mimo.app.util.TimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(onBack: () -> Unit, viewModel: StatsViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your progress") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Text("Today", style = MaterialTheme.typography.labelLarge)
                        Text("${state.closuresToday}", style = MaterialTheme.typography.headlineLarge)
                        Text("apps closed")
                    }
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Text("Time held", style = MaterialTheme.typography.labelLarge)
                        Text(
                            TimeUtils.formatDuration(state.timeGuardedTodayMillis),
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text("guarded today")
                    }
                }
            }

            item {
                Text("Most guarded apps", style = MaterialTheme.typography.titleLarge)
            }
            items(state.topApps) { app ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(app.appLabel)
                        Text("${app.opens}× closed")
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("Recent activity", style = MaterialTheme.typography.titleLarge)
            }
            items(state.recentLogs) { log ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(log.appLabel, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Closed ${TimeUtils.formatClock(log.timestamp)} · allowed ${TimeUtils.formatDuration(log.allowedDurationMillis)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = viewModel::clearHistory, modifier = Modifier.fillMaxWidth()) {
                    Text("Clear history")
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
