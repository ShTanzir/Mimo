package com.mimo.app.ui.applist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mimo.app.ui.theme.Clay
import com.mimo.app.util.InstalledApp
import com.mimo.app.util.TimeUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    onAppClick: (String) -> Unit,
    onOpenStats: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenFocusSession: () -> Unit,
    viewModel: AppListViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var searchExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(searchExpanded) {
        if (searchExpanded) {
            delay(80)
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box {
                        AnimatedVisibility(
                            visible = !searchExpanded,
                            enter = fadeIn(tween(150)),
                            exit = fadeOut(tween(100))
                        ) { Text("MIMO") }
                        AnimatedVisibility(
                            visible = searchExpanded,
                            enter = fadeIn(tween(150)) + expandHorizontally(tween(200)),
                            exit = fadeOut(tween(100)) + shrinkHorizontally(tween(150))
                        ) {
                            OutlinedTextField(
                                value = state.query,
                                onValueChange = viewModel::setQuery,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                placeholder = { Text("Search apps") },
                                singleLine = true
                            )
                        }
                    }
                },
                actions = {
                    if (searchExpanded) {
                        IconButton(onClick = {
                            searchExpanded = false
                            viewModel.setQuery("")
                        }) { Icon(Icons.Filled.Close, contentDescription = "Close search") }
                    } else {
                        IconButton(onClick = { searchExpanded = true }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = onOpenFocusSession) {
                            Icon(Icons.Filled.Bolt, contentDescription = "Focus session")
                        }
                        IconButton(onClick = onOpenStats) { Icon(Icons.Filled.Shield, contentDescription = "Stats") }
                        IconButton(onClick = onOpenSettings) { Icon(Icons.Filled.Settings, contentDescription = "Settings") }
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
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
            .animateContentSize(animationSpec = tween(150))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    )
}
