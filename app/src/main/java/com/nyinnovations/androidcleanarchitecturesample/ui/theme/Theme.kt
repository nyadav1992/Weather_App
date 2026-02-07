package com.nyinnovations.androidcleanarchitecturesample.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PremiumDarkScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = DeepNavy,
    secondary = AccentBlue,
    onSecondary = DeepNavy,
    tertiary = MutedGold,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = DarkBlue,
    onSurface = TextPrimary,
    surfaceVariant = CardSurface,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White,
    secondaryContainer = CardSurfaceLight,
    onSecondaryContainer = TextPrimary
)

@Composable
fun AndroidCleanArchitectureSampleTheme(
    content: @Composable () -> Unit
) {
    // always dark — premium feel
    MaterialTheme(
        colorScheme = PremiumDarkScheme,
        typography = Typography,
        content = content
    )
}