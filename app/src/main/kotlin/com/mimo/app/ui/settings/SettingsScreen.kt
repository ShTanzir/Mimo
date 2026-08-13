package com.mimo.app.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mimo.app.ui.components.GlassCard
import com.mimo.app.util.BackupFileUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenPermissions: () -> Unit,
    onOpenAbout: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            try {
                val text = BackupFileUtil.readText(context, uri)
                if (text != null) {
                    val count = viewModel.importRulesJson(text)
                    snackbarHostState.showSnackbar("Imported $count rule(s).")
                } else {
                    snackbarHostState.showSnackbar("Couldn't read that file.")
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("That doesn't look like a MIMO backup file.")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .animateContentSize(tween(150)),
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

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Backup & restore", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Export your guarded-app rules to a JSON file, or restore them on a new device.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                val json = viewModel.exportRulesJson()
                                val uri = BackupFileUtil.writeBackupFile(context, json)
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "application/json"
                                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(android.content.Intent.createChooser(intent, "Share MIMO backup"))
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Export") }

                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf("application/json")) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Import") }
                }
            }

            OutlinedButton(onClick = onOpenPermissions, modifier = Modifier.fillMaxWidth()) {
                Text("Review permissions")
            }

            OutlinedButton(onClick = onOpenAbout, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("About MIMO")
            }

            Spacer(Modifier.weight(1f))
            Text(
                "MIMO v1.2.0 · Built with care for calmer screen time.",
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
