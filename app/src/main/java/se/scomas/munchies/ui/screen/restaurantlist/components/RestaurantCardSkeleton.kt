package se.scomas.munchies.ui.screen.restaurantlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import se.scomas.munchies.ui.components.shimmerBrush
import se.scomas.munchies.ui.theme.MunchiesTheme
import se.scomas.munchies.ui.theme.munchiesColors
import se.scomas.munchies.ui.theme.radius
import se.scomas.munchies.ui.theme.spacing

@Composable
fun RestaurantCardSkeleton(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    val colors = MaterialTheme.munchiesColors

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.radius.card,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(MaterialTheme.radius.image)
                    .background(brush)
            )

            Column(
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.spacing.md,
                    vertical = MaterialTheme.spacing.sm
                )
            ) {
                // Title placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(16.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(brush)
                )
                Spacer(Modifier.height(MaterialTheme.spacing.sm))
                // Meta row placeholders
                Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(12.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .height(12.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(brush)
                    )
                }
                Spacer(Modifier.height(MaterialTheme.spacing.xs))
            }
        }
    }
}

@Preview(name = "Skeleton – Light", showBackground = true, widthDp = 360)
@Composable
private fun SkeletonLightPreview() {
    MunchiesTheme(darkTheme = false) { RestaurantCardSkeleton() }
}

@Preview(name = "Skeleton – Dark", showBackground = true, backgroundColor = 0xFF121212, widthDp = 360)
@Composable
private fun SkeletonDarkPreview() {
    MunchiesTheme(darkTheme = true) { RestaurantCardSkeleton() }
}