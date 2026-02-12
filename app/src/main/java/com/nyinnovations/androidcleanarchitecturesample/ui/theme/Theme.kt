package com.nyinnovations.androidcleanarchitecturesample.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // ensure light icons on dark background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = PremiumDarkScheme,
        typography = Typography,
        content = content
    )
}