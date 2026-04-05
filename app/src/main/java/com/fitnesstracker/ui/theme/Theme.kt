package com.fitnesstracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Indigo80,
    secondary = IndigoGrey80,
    tertiary = Teal80
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo40,
    secondary = IndigoGrey40,
    tertiary = Teal40
)

@Composable
fun FitnessTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color uses the device wallpaper to generate a color scheme (Android 12+).
    // Disabled by default so the app always renders the defined palette above.
    // Pass dynamicColor = true to opt back in.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            // Keep dynamic color support available as an opt-in.
            // Requires android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S check
            // if re-enabling — add imports for dynamicDarkColorScheme/dynamicLightColorScheme.
            if (darkTheme) DarkColorScheme else LightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}