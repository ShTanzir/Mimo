package com.mimo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.mimo.app.ui.theme.GlassBorder
import com.mimo.app.ui.theme.GlassWhite
import com.mimo.app.ui.theme.GlassWhiteSoft

/**
 * A soft, semi-transparent "glass" surface used throughout MIMO's screens.
 * v1.2.0: adds a hairline border + gentle shadow so the glass effect reads
 * clearly against the light-green background, with animation-friendly
 * defaults (works nicely inside AnimatedVisibility / animateContentSize).
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 24.dp,
    content: @Composable ColumnScopeAlias.() -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(cornerRadius), clip = false)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    listOf(GlassWhite, GlassWhiteSoft, scheme.surface.copy(alpha = 0.55f))
                )
            )
            .border(1.dp, GlassBorder, RoundedCornerShape(cornerRadius))
            .padding(20.dp),
        content = content
    )
}

typealias ColumnScopeAlias = androidx.compose.foundation.layout.ColumnScope
