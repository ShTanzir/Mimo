package com.mimo.app.ui.appdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mimo.app.ui.components.GlassCard
import com.mimo.app.util.TimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    packageName: String,
    onBack: () -> Unit,
    viewModel: AppDetailViewModel = viewModel()
) {
    LaunchedEffect(packageName) { viewModel.load(packageName) }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.appLabel) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    if (state.ruleExists) {
                        IconButton(onClick = { viewModel.deleteRule(); onBack() }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remove rule")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Close after", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                "Pick how long you're allowed in ${state.appLabel} before MIMO steps in.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(16.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(TimeUtils.presets) { preset ->
                    val selected = state.selectedPresetLabel == preset.label
                    FilterChip(
                        selected = selected,
                        onClick = {
                            if (preset.label == "Custom") {
                                viewModel.applyCustomTime()
                            } else {
                                viewModel.selectPreset(preset.label, preset.millis)
                            }
                        },
                        label = { Text(preset.label) }
                    )
                }
            }

            if (state.selectedPresetLabel == "Custom") {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.customMinutes,
                        onValueChange = { viewModel.setCustomMinutes(it); viewModel.applyCustomTime() },
                        label = { Text("Minutes") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.customSeconds,
                        onValueChange = { viewModel.setCustomSeconds(it); viewModel.applyCustomTime() },
                        label = { Text("Seconds") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Selected delay", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    TimeUtils.formatDuration(state.selectedDelayMillis),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = state.closeMessage,
                onValueChange = viewModel::setCloseMessage,
                label = { Text("Message shown when closing") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Allow one-time snooze")
                Switch(checked = state.allowSnoozeOnce, onCheckedChange = viewModel::setAllowSnooze)
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Vibrate before closing")
                Switch(checked = state.vibrateOnWarning, onCheckedChange = viewModel::setVibrate)
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { viewModel.save(); onBack() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (state.ruleExists) "Update rule" else "Guard this app")
            }
        }
    }
}
