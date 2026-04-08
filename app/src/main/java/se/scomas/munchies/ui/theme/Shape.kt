package se.scomas.munchies.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

// Material3 shape scale — used by Material components automatically.
val MunchiesShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

// Semantic aliases for direct use in composables.
@Immutable
data class MunchiesRadius(
    val chip: RoundedCornerShape   = RoundedCornerShape(50),   // pill
    val card: RoundedCornerShape   = RoundedCornerShape(16.dp),
    val image: RoundedCornerShape  = RoundedCornerShape(12.dp),
    val badge: RoundedCornerShape  = RoundedCornerShape(50),   // pill
)

internal val LocalRadius = staticCompositionLocalOf { MunchiesRadius() }
