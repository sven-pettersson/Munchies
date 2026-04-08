package se.scomas.munchies.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Palette ───────────────────────────────────────────────────────────────────
// Raw values — not used directly in UI. Reference via MunchiesColors instead.

internal val Neutral0    = Color(0xFFFFFFFF)
internal val Neutral50   = Color(0xFFF5F5F5)
internal val Neutral100  = Color(0xFFEEEEEE)
internal val Neutral300  = Color(0xFFB0B0B0)
internal val Neutral500  = Color(0xFF767676)
internal val Neutral800  = Color(0xFF1A1A1A)
internal val Neutral900  = Color(0xFF0F0F0F)

internal val Green400    = Color(0xFF2ECB71)
internal val Red400      = Color(0xFFFF4D4D)
internal val Yellow400   = Color(0xFFFFC529)

internal val Dark200     = Color(0xFF2C2C2E)
internal val Dark300     = Color(0xFF3A3A3C)
internal val Dark700     = Color(0xFF1C1C1E)
internal val Dark900     = Color(0xFF121212)

// ── Semantic token class ──────────────────────────────────────────────────────

@Immutable
data class MunchiesColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    // Status
    val openGreen: Color,
    val closedRed: Color,
    val starYellow: Color,
    // Filter chips
    val chipSelectedBackground: Color,
    val chipSelectedContent: Color,
    val chipUnselectedBackground: Color,
    val chipUnselectedContent: Color,
)

internal val LightColors = MunchiesColors(
    background            = Neutral0,
    surface               = Neutral50,
    surfaceVariant        = Neutral100,
    onBackground          = Neutral800,
    onSurface             = Neutral800,
    onSurfaceVariant      = Neutral500,
    outline               = Neutral100,
    openGreen             = Green400,
    closedRed             = Red400,
    starYellow            = Yellow400,
    chipSelectedBackground   = Neutral800,
    chipSelectedContent      = Neutral0,
    chipUnselectedBackground = Neutral100,
    chipUnselectedContent    = Neutral800,
)

internal val DarkColors = MunchiesColors(
    background            = Dark900,
    surface               = Dark700,
    surfaceVariant        = Dark200,
    onBackground          = Neutral0,
    onSurface             = Neutral0,
    onSurfaceVariant      = Neutral300,
    outline               = Dark300,
    openGreen             = Green400,
    closedRed             = Red400,
    starYellow            = Yellow400,
    chipSelectedBackground   = Neutral0,
    chipSelectedContent      = Neutral900,
    chipUnselectedBackground = Dark200,
    chipUnselectedContent    = Neutral0,
)

internal val LocalMunchiesColors = staticCompositionLocalOf { LightColors }
