package se.scomas.munchies.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Material3 color schemes (mapped from Munchies palette) ───────────────────

private val M3LightColorScheme = lightColorScheme(
    primary          = Neutral800,
    onPrimary        = Neutral0,
    background       = Neutral0,
    onBackground     = Neutral800,
    surface          = Neutral50,
    onSurface        = Neutral800,
    surfaceVariant   = Neutral100,
    onSurfaceVariant = Neutral500,
    outline          = Neutral100,
)

private val M3DarkColorScheme = darkColorScheme(
    primary          = Neutral0,
    onPrimary        = Neutral900,
    background       = Dark900,
    onBackground     = Neutral0,
    surface          = Dark700,
    onSurface        = Neutral0,
    surfaceVariant   = Dark200,
    onSurfaceVariant = Neutral300,
    outline          = Dark300,
)

// ── Theme entry point ─────────────────────────────────────────────────────────

@Composable
fun MunchiesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) M3DarkColorScheme else M3LightColorScheme
    val munchiesColors = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalMunchiesColors provides munchiesColors,
        LocalSpacing provides Spacing(),
        LocalRadius provides MunchiesRadius(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MunchiesTypography,
            shapes = MunchiesShapes,
            content = content
        )
    }
}

// ── MaterialTheme extensions — use these in composables ──────────────────────

val MaterialTheme.munchiesColors: MunchiesColors
    @Composable @ReadOnlyComposable
    get() = LocalMunchiesColors.current

val MaterialTheme.spacing: Spacing
    @Composable @ReadOnlyComposable
    get() = LocalSpacing.current

val MaterialTheme.radius: MunchiesRadius
    @Composable @ReadOnlyComposable
    get() = LocalRadius.current
