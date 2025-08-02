package com.anti.rootadbcontroller.ui.theme

import androidx.compose.material.darkColors
    primary = PrimaryGreen,
    onPrimary = Color.Black,

) {
        // You could define a LightColorPalette here if needed.
        colors = colors,
}
import androidx.compose.material.MaterialTheme
private val DarkColorPalette = darkColors(
    surface = DarkSurface,
)
    content: @Composable () -> Unit
        // For now, we'll force dark theme.
    MaterialTheme(
    )
import androidx.compose.foundation.isSystemInDarkTheme

    background = DarkBackground,
    onSurface = DarkOnSurface
    darkTheme: Boolean = isSystemInDarkTheme(),
    } else {

        content = content
import androidx.compose.ui.graphics.Color
    secondary = AccentRed,
    onBackground = DarkOnSurface,
fun RootADBControllerTheme(
        DarkColorPalette
    }
        shapes = Shapes,
import androidx.compose.runtime.Composable
    primaryVariant = PrimaryGreen,
    onSecondary = Color.White,
@Composable
    val colors = if (darkTheme) {
        DarkColorPalette
        typography = Typography,
