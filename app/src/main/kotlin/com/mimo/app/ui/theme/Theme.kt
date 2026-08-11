package com.mimo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = SageDark,
    onPrimary = Cream,
    secondary = Clay,
    onSecondary = Cream,
    background = Cream,
    onBackground = Ink,
    surface = Color.WhiteBridge,
    onSurface = Ink,
    surfaceVariant = CreamDim,
    error = DangerRed
)

private val DarkColors = darkColorScheme(
    primary = SageLight,
    onPrimary = MossDeep,
    secondary = Clay,
    onSecondary = MossDeep,
    background = MossDeep,
    onBackground = Cream,
    surface = Ink,
    onSurface = Cream,
    surfaceVariant = InkSoft,
    error = DangerRed
)

// small helper object to avoid importing androidx.compose.ui.graphics.Color name clash
private object Color {
    val WhiteBridge = androidx.compose.ui.graphics.Color(0xFFFFFFFF)
}

@Composable
fun MimoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = MimoTypography,
        content = content
    )
}
