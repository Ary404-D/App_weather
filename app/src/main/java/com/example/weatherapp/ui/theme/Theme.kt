package com.example.weatherapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    secondary = Cyan80,
    tertiary = BlueGrey80,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = CardDark,
    onPrimary = BackgroundDark,
    onSecondary = BackgroundDark,
    onTertiary = BackgroundDark,
    onBackground = SurfaceLight,
    onSurface = SurfaceLight,
    onSurfaceVariant = SurfaceLight
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    secondary = Cyan40,
    tertiary = BlueGrey40,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = CardLight,
    onPrimary = SurfaceLight,
    onSecondary = SurfaceLight,
    onTertiary = SurfaceLight,
    onBackground = BackgroundDark,
    onSurface = BackgroundDark,
    onSurfaceVariant = BackgroundDark
)

@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
