package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val OmniFrostedGlassColorScheme = darkColorScheme(
    primary = LavenderPrimary,
    onPrimary = DeepPurpleAccent,
    primaryContainer = PurpleGradientStart,
    onPrimaryContainer = LavenderPrimary,
    secondary = ActivePillBg,
    onSecondary = ActivePillText,
    secondaryContainer = GlassSurfaceVariant,
    onSecondaryContainer = LavenderPrimary,
    tertiary = NeonPink,
    onTertiary = GlassBackground,
    background = GlassBackground,
    onBackground = TextPrimary,
    surface = GlassSurface,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder
)

@Composable
fun OmniHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = OmniFrostedGlassColorScheme,
        typography = Typography,
        content = content
    )
}

