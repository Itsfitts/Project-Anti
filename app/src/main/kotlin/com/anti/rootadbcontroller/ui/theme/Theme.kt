package com.anti.rootadbcontroller.ui.theme

import android.app.Activity

fun RootADBControllerTheme(
    val view = LocalView.current
}
import androidx.core.view.WindowCompat
@Composable
    }
    )
import androidx.compose.ui.platform.LocalView

        else -> LightColorScheme
        content = content
import androidx.compose.ui.platform.LocalContext
)
        darkTheme -> DarkColorScheme
        typography = Typography,
import androidx.compose.ui.graphics.toArgb
    tertiary = Pink40

        colorScheme = colorScheme,
import androidx.compose.runtime.SideEffect
    secondary = PurpleGrey40,
        }
    MaterialTheme(
import androidx.compose.runtime.Composable
    primary = Purple40,
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

import androidx.compose.material3.lightColorScheme
private val LightColorScheme = lightColorScheme(
            val context = LocalContext.current
    }
import androidx.compose.material3.dynamicLightColorScheme

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        }
import androidx.compose.material3.dynamicDarkColorScheme
)
    val colorScheme = when {
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
import androidx.compose.material3.darkColorScheme
    tertiary = Pink80
) {
            window.statusBarColor = colorScheme.primary.toArgb()
import androidx.compose.material3.MaterialTheme
    secondary = PurpleGrey80,
    content: @Composable () -> Unit
            val window = (view.context as Activity).window
import androidx.compose.foundation.isSystemInDarkTheme
    primary = Purple80,
    dynamicColor: Boolean = true,
        SideEffect {
import android.os.Build
private val DarkColorScheme = darkColorScheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    if (!view.isInEditMode) {
