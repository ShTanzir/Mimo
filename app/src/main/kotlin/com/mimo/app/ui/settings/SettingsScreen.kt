package com.mimo.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mimo.app.ui.components.GlassCard
import com.mimo.app.util.PermissionUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenPermissions: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("MIMO is active", style = MaterialTheme.typography.titleMedium)
                        Text("Turn off to pause all guarding temporarily.", style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(checked = state.masterEnabled, onCheckedChange = viewModel::setMasterEnabled)
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dark theme", style = MaterialTheme.typography.titleMedium)
                    Switch(checked = state.darkTheme, onCheckedChange = viewModel::setDarkTheme)
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("PIN protection", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Require a PIN to change MIMO's rules, so willpower isn't the only line of defense.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Switch(
                        checked = state.pinEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) { showPinDialog = true } else { viewModel.setPin(null) }
                        }
                    )
                }
            }

            OutlinedButton(onClick = onOpenPermissions, modifier = Modifier.fillMaxWidth()) {
                Text("Review permissions")
            }

            Spacer(Modifier.weight(1f))
            Text(
                "MIMO v1.0.0 · Built with care for calmer screen time.",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Set a 4-digit PIN") },
            text = {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) pinInput = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (pinInput.length == 4) {
                        viewModel.setPin(pinInput)
                        showPinDialog = false
                        pinInput = ""
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) { Text("Cancel") }
            }
        )
    }
}
