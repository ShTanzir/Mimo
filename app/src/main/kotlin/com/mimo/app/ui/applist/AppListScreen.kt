package com.mimo.app.ui.applist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mimo.app.ui.theme.Clay
import com.mimo.app.util.InstalledApp
import com.mimo.app.util.TimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    onAppClick: (String) -> Unit,
    onOpenStats: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: AppListViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MIMO") },
                actions = {
                    IconButton(onClick = onOpenStats) { Icon(Icons.Filled.Shield, contentDescription = "Stats") }
                    IconButton(onClick = onOpenSettings) { Icon(Icons.Filled.Settings, contentDescription = "Settings") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::setQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search apps") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = state.showOnlyGuarded,
                    onClick = viewModel::toggleShowOnlyGuarded,
                    label = { Text("Guarded only") }
                )
                Spacer(Modifier.width(8.dp))
                FilterChip(
                    selected = state.showSystemApps,
                    onClick = { viewModel.loadApps(!state.showSystemApps) },
                    label = { Text("Include system apps") }
                )
            }

            Spacer(Modifier.height(8.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val apps = viewModel.filteredApps()
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(apps, key = { it.packageName }) { app ->
                        AppRow(
                            app = app,
                            delayLabel = state.rules[app.packageName]?.let {
                                if (it.enabled) TimeUtils.formatDuration(it.delayMillis) else "Paused"
                            },
                            onClick = { onAppClick(app.packageName) }
                        )
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun AppRow(app: InstalledApp, delayLabel: String?, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(app.label) },
        supportingContent = {
            if (delayLabel != null) Text("Closes after: $delayLabel")
            else Text("Not guarded", color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        leadingContent = {
            val bitmap = remember(app.packageName) { app.icon?.toBitmap()?.asImageBitmap() }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                )
            } else {
                Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)))
            }
        },
        trailingContent = {
            if (delayLabel != null) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Clay.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Set", color = Clay, style = MaterialTheme.typography.labelMedium)
                }
            }
        },
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    )
}
