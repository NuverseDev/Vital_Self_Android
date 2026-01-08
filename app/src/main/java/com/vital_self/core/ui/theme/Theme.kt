package com.vital_self.core.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = ThemeColor,
    onPrimary = White,
    primaryContainer = ThemeColorSecondary,
    onPrimaryContainer = White,
    secondary = ThemeColor,
    onSecondary = White,
    secondaryContainer = PrimaryButtonColor,
    onSecondaryContainer = SecondaryFrontColor,
    tertiary = ThemeBtnClickedColor,
    onTertiary = White,
    background = PrimaryBackgroundColor,
    onBackground = PrimaryTextColor,
    surface = White,
    onSurface = PrimaryTextColor,
    surfaceVariant = EditTextBackground,
    onSurfaceVariant = SecondaryTextColor,
    error = ErrorMsgTextColor,
    onError = White,
    outline = EditTextBorder,
    outlineVariant = DividerColor
)

private val DarkColorScheme = darkColorScheme(
    primary = ThemeColor,
    onPrimary = White,
    primaryContainer = ThemeColorSecondary,
    onPrimaryContainer = White,
    secondary = ThemeColor,
    onSecondary = White,
    secondaryContainer = SplashGradientCenter,
    onSecondaryContainer = White,
    tertiary = ThemeBtnClickedColor,
    onTertiary = White,
    background = SplashGradientStart,
    onBackground = White,
    surface = SplashGradientCenter,
    onSurface = White,
    surfaceVariant = SplashGradientCenter,
    onSurfaceVariant = TaglineColor,
    error = ErrorMsgTextColor,
    onError = White,
    outline = GlowRingColor,
    outlineVariant = GlowRingColor
)

@Composable
fun VitalSelfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) Black.toArgb() else White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VitalSelfTypography,
        shapes = VitalSelfShapes,
        content = content
    )
}
