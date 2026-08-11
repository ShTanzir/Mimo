package com.mimo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

/** A soft, semi-transparent "glass" surface used throughout MIMO's screens. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScopeAlias.() -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        scheme.surface.copy(alpha = 0.75f),
                        scheme.surfaceVariant.copy(alpha = 0.55f)
                    )
                )
            )
            .padding(20.dp),
        content = content
    )
}

typealias ColumnScopeAlias = androidx.compose.foundation.layout.ColumnScope
