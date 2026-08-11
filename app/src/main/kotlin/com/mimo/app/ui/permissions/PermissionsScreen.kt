package com.mimo.app.ui.permissions

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mimo.app.ui.components.GlassCard
import com.mimo.app.util.PermissionUtils

private data class PermRow(
    val title: String,
    val description: String,
    val isGranted: () -> Boolean,
    val onRequest: () -> Unit
)

@Composable
fun PermissionsScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    var refreshTick by remember { mutableStateOf(0) }

    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        refreshTick++
    }

    val rows = remember(refreshTick) {
        buildList {
            add(
                PermRow(
                    "Accessibility Service",
                    "Required so MIMO can detect when a guarded app opens.",
                    { PermissionUtils.isAccessibilityServiceEnabled(context) },
                    { context.startActivity(PermissionUtils.accessibilitySettingsIntent()) }
                )
            )
            add(
                PermRow(
                    "Display over other apps",
                    "Lets MIMO show its countdown warning above other apps.",
                    { PermissionUtils.canDrawOverlays(context) },
                    { context.startActivity(PermissionUtils.overlaySettingsIntent(context)) }
                )
            )
            if (PermissionUtils.notificationsPermissionRequired()) {
                add(
                    PermRow(
                        "Notifications",
                        "Shows the live countdown while an app is about to close.",
                        {
                            androidx.core.content.ContextCompat.checkSelfPermission(
                                context, Manifest.permission.POST_NOTIFICATIONS
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        },
                        { notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
                    )
                )
            }
            add(
                PermRow(
                    "Ignore battery optimization",
                    "Keeps MIMO's guard running reliably in the background.",
                    { PermissionUtils.isIgnoringBatteryOptimizations(context) },
                    { context.startActivity(PermissionUtils.batteryOptimizationIntent(context)) }
                )
            )
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("A few permissions", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "MIMO needs these to close apps on your behalf. Nothing you type or view in other apps is ever read.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(24.dp))

        LazyColumnPermissions(rows)

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) { Text("Continue") }
    }
}

@Composable
private fun LazyColumnPermissions(rows: List<PermRow>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { row ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val granted = row.isGranted()
                    Icon(
                        if (granted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(row.title, style = MaterialTheme.typography.titleMedium)
                        Text(row.description, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (!granted) {
                        TextButton(onClick = row.onRequest) { Text("Enable") }
                    }
                }
            }
        }
    }
}
