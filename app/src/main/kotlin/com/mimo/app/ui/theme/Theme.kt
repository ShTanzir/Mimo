package com.mimo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = MintDeep,
    onPrimary = Cream,
    secondary = Clay,
    onSecondary = Cream,
    background = MintPale,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink,
    surfaceVariant = CreamDim,
    primaryContainer = MintLight,
    onPrimaryContainer = Ink,
    error = DangerRed
)

private val DarkColors = darkColorScheme(
    primary = MintLight,
    onPrimary = ForestDeep,
    secondary = Clay,
    onSecondary = ForestDeep,
    background = ForestDeep,
    onBackground = Cream,
    surface = Ink,
    onSurface = Cream,
    surfaceVariant = InkSoft,
    primaryContainer = MintDeep,
    onPrimaryContainer = Cream,
    error = DangerRed
)

/**
 * MIMO's theme: light green base with a glassmorphism surface language.
 * [darkTheme] is driven explicitly by user preference (Settings), not just
 * the system setting, so it stays consistent with what the person picked.
 */
@Composable
fun MimoTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = MimoTypography,
        content = content
    )
}
