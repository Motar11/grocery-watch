package com.motar11.grocerywatch.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat

private val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = Forest,
    onPrimary = Mist,
    primaryContainer = Mint,
    onPrimaryContainer = Stone,
    secondary = Stone,
    onSecondary = Mist,
    background = Mist,
    surface = Mist,
    onBackground = Stone,
    onSurface = Stone,
    tertiary = Sun
)

private val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = Mint,
    onPrimary = Stone,
    primaryContainer = Forest,
    onPrimaryContainer = Mist,
    secondary = Mist,
    onSecondary = Stone,
    background = Stone,
    surface = Stone,
    onBackground = Mist,
    onSurface = Mist,
    tertiary = Sun
)

@Composable
fun GroceryWatchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalContext.current as? Activity
    SideEffect {
        view?.let {
            it.window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(it.window, it.window.decorView).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
