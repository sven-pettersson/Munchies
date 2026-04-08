package se.scomas.munchies.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.4f),
        Color.LightGray.copy(alpha = 0.15f),
        Color.LightGray.copy(alpha = 0.4f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 400f, y = 0f),
        end = Offset(x = translateAnim, y = 0f)
    )
}

