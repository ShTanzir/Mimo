package com.mimo.app.ui.focus

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mimo.app.ui.components.GlassCard
import com.mimo.app.util.FocusSessionManager
import com.mimo.app.util.TimeUtils
import kotlinx.coroutines.delay

private val durations = listOf("15 min" to 15L, "25 min" to 25L, "45 min" to 45L, "60 min" to 60L)

/**
 * Pomodoro-style Focus Session: while running, MIMO closes any app that
 * isn't the phone dialer, default SMS app, or MIMO itself — regardless of
 * whether it has a saved rule.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusSessionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var tick by remember { mutableStateOf(0L) }
    var selectedMinutes by remember { mutableStateOf(25L) }

    LaunchedEffect(Unit) {
        while (true) {
            tick = FocusSessionManager.remainingMillis(context)
            delay(1000)
        }
    }

    val active = tick > 0
    val total = FocusSessionManager.totalDurationMillis(context).coerceAtLeast(1L)
    val progress = if (active) (tick.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Focus Session") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Icon(Icons.Filled.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                if (active) "Focus session running" else "Start a distraction-free block",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Every app closes instantly during a session — except calls and texts.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(32.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        if (active) TimeUtils.formatShortTimer(tick) else "Ready",
                        style = MaterialTheme.typography.displayLarge
                    )
                    if (active) {
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            if (!active) {
                Text("Choose a length", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    durations.forEach { (label, minutes) ->
                        FilterChip(
                            selected = selectedMinutes == minutes,
                            onClick = { selectedMinutes = minutes },
                            label = { Text(label) }
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            if (active) {
                OutlinedButton(
                    onClick = {
                        FocusSessionManager.stop(context)
                        tick = 0L
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) { Text("End session early") }
            } else {
                Button(
                    onClick = {
                        FocusSessionManager.start(context, selectedMinutes * 60_000L)
                        tick = FocusSessionManager.remainingMillis(context)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) { Text("Start $selectedMinutes-minute session") }
            }
        }
    }
}
