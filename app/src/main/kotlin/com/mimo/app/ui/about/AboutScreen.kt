package com.mimo.app.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mimo.app.BuildConfig
import com.mimo.app.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About MIMO") },
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("MIMO", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "v${BuildConfig.VERSION_NAME} (build ${BuildConfig.VERSION_CODE})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "MIMO is a mindful app closer: pick the apps that pull you in, set a delay, " +
                        "and MIMO gently steps in and closes them once your time is up.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Shield, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text("Privacy first", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "MIMO runs completely offline. It never reads screen content or personal " +
                        "data from other apps — only the package name of whichever app is in " +
                        "front, so it can decide whether a rule applies. All rules and history " +
                        "stay on this device.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Code, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text("Built with", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Kotlin · Jetpack Compose · Material 3 · Room · DataStore · " +
                        "AccessibilityService — built with care for calmer screen time.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Menu, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text("Package", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(8.dp))
                Text(BuildConfig.APPLICATION_ID, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
