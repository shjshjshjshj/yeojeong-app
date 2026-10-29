package com.tts.yeojeong.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val YeojeongColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Soft,
    onPrimaryContainer = Primary,
    secondary = Accent,
    onSecondary = Color.White,
    secondaryContainer = Soft,
    onSecondaryContainer = Ink,
    tertiary = Highlight,
    onTertiary = Ink,
    background = Page,
    onBackground = Ink,
    surface = SurfaceWhite,
    onSurface = Ink,
    surfaceVariant = Soft,
    onSurfaceVariant = TextSecondary,
    outline = Disabled,
    outlineVariant = Soft,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val YeojeongShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun YeojeongTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = YeojeongColorScheme,
        typography = YeojeongTypography,
        shapes = YeojeongShapes,
        content = content
    )
}

@Suppress("DEPRECATION")
@Composable
fun SystemBarIcons(useDarkIcons: Boolean) {
    val view = LocalView.current
    val context = LocalContext.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = useDarkIcons
                isAppearanceLightNavigationBars = true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val flags = window.decorView.systemUiVisibility
                window.decorView.systemUiVisibility = if (useDarkIcons) {
                    flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
            }
        }
    }
}
