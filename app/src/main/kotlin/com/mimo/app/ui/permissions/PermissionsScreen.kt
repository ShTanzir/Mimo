package com.mimo.app.ui.permissions

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.mimo.app.ui.components.GlassCard
import com.mimo.app.util.PermissionUtils
import kotlinx.coroutines.launch

private data class PermRow(
    val title: String,
    val description: String,
    val instructions: String,
    val isGranted: () -> Boolean,
    val onRequest: () -> Unit
)

/**
 * Shown on first launch, and again on every app open/resume until every
 * critical permission is granted. This is intentional: MIMO can't guard
 * anything without these, so it keeps asking rather than silently failing.
 */
@Composable
fun PermissionsScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var refreshTick by remember { mutableStateOf(0) }

    // Re-check permission state every time the user returns to the app
    // (e.g. coming back from the system Settings screen).
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshTick++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        refreshTick++
    }

    val rows = remember(refreshTick) {
        buildList {
            add(
                PermRow(
                    title = "Accessibility Service",
                    description = "Required so MIMO can detect when a guarded app opens.",
                    instructions = "Tap Enable → find MIMO in the list → turn the switch on → confirm.",
                    isGranted = { PermissionUtils.isAccessibilityServiceEnabled(context) },
                    onRequest = { context.startActivity(PermissionUtils.accessibilitySettingsIntent()) }
                )
            )
            add(
                PermRow(
                    title = "Display over other apps",
                    description = "Lets MIMO show its full-screen countdown warning above other apps.",
                    instructions = "Tap Enable → allow \"Display over other apps\" for MIMO.",
                    isGranted = { PermissionUtils.canDrawOverlays(context) },
                    onRequest = { context.startActivity(PermissionUtils.overlaySettingsIntent(context)) }
                )
            )
            if (PermissionUtils.notificationsPermissionRequired()) {
                add(
                    PermRow(
                        title = "Notifications",
                        description = "Shows the live countdown while an app is about to close.",
                        instructions = "Tap Enable and allow notifications when Android asks.",
                        isGranted = {
                            androidx.core.content.ContextCompat.checkSelfPermission(
                                context, Manifest.permission.POST_NOTIFICATIONS
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        },
                        onRequest = { notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
                    )
                )
            }
            add(
                PermRow(
                    title = "Ignore battery optimization",
                    description = "Keeps MIMO's guard running reliably in the background. Recommended, not required.",
                    instructions = "Tap Enable → choose \"Allow\" / \"Don't optimize\" for MIMO.",
                    isGranted = { PermissionUtils.isIgnoringBatteryOptimizations(context) },
                    onRequest = { context.startActivity(PermissionUtils.batteryOptimizationIntent(context)) }
                )
            )
        }
    }

    val criticalGranted = remember(refreshTick) { PermissionUtils.allCriticalGranted(context) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Icon(Icons.Filled.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Text("A few permissions", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "MIMO needs these to close apps on your behalf. Nothing you type or view in other apps is ever read.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rows.forEach { row -> PermissionRow(row) }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (PermissionUtils.allCriticalGranted(context)) {
                        onContinue()
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Please enable all required permissions above to continue."
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) { Text(if (criticalGranted) "Continue" else "Grant permissions to continue") }
        }
    }
}

@Composable
private fun PermissionRow(row: PermRow) {
    var expanded by remember { mutableStateOf(false) }
    val granted = row.isGranted()

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(200))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                if (granted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(12.dp))
            Column(
                Modifier
                    .weight(1f)
                    .let { if (!granted) it else it }
            ) {
                Text(row.title, style = MaterialTheme.typography.titleMedium)
                Text(row.description, style = MaterialTheme.typography.bodyMedium)
            }
            if (!granted) {
                TextButton(onClick = { expanded = !expanded }) { Text(if (expanded) "Hide" else "How?") }
            }
        }

        AnimatedVisibility(
            visible = expanded && !granted,
            enter = fadeIn(tween(150)) + expandVertically(tween(200)),
            exit = fadeOut(tween(100)) + shrinkVertically(tween(150))
        ) {
            Column(Modifier.padding(top = 8.dp)) {
                Text(
                    row.instructions,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = row.onRequest, modifier = Modifier.fillMaxWidth()) {
                    Text("Enable")
                }
            }
        }
    }
}
