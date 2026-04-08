package se.scomas.munchies.ui.screen.restaurantlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import se.scomas.munchies.ui.theme.MunchiesTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import se.scomas.munchies.R
import se.scomas.munchies.ui.screen.restaurantlist.RestaurantUiModel
import se.scomas.munchies.ui.theme.munchiesColors
import se.scomas.munchies.ui.theme.radius
import se.scomas.munchies.ui.theme.spacing

@Composable
fun RestaurantCard(
    restaurant: RestaurantUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.munchiesColors

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.radius.card,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // ── Hero image ────────────────────────────────────────────────────
            Box {
                AsyncImage(
                    model = restaurant.imageUrl,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(MaterialTheme.radius.image)
                )
                // Open / closed badge
                restaurant.isOpen?.let { isOpen ->
                    OpenStatusBadge(
                        isOpen = isOpen,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(MaterialTheme.spacing.sm)
                    )
                }
            }

            // ── Info ──────────────────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.spacing.md,
                    vertical = MaterialTheme.spacing.sm
                )
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface
                )
                Row(
                    modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingBadge(rating = restaurant.rating)
                    DeliveryTimeBadge(minutes = restaurant.deliveryTimeMinutes)
                }
            }
        }
    }
}

@Composable
private fun OpenStatusBadge(isOpen: Boolean, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.munchiesColors
    val color = if (isOpen) colors.openGreen else colors.closedRed
    val label = if (isOpen) "Open" else "Closed"

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        modifier = modifier
            .clip(MaterialTheme.radius.badge)
            .background(color)
            .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
    )
}

@Composable
private fun RatingBadge(rating: Double, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.ic_star),
            contentDescription = null,
            tint = MaterialTheme.munchiesColors.starYellow,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(2.dp))
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.munchiesColors.onSurfaceVariant
        )
    }
}

@Composable
private fun DeliveryTimeBadge(minutes: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = null,
            tint = MaterialTheme.munchiesColors.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(2.dp))
        Text(
            text = "$minutes min",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.munchiesColors.onSurfaceVariant
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewRestaurant = RestaurantUiModel(
    id = "1",
    name = "Wayne \"Chad Broski\" Burgers",
    rating = 4.6,
    deliveryTimeMinutes = 9,
    imageUrl = "https://food-delivery.umain.io/images/restaurant/burgers.png",
    isOpen = true
)

@Preview(name = "Card – Open – Light", showBackground = true, widthDp = 360)
@Composable
private fun RestaurantCardOpenLightPreview() {
    MunchiesTheme(darkTheme = false) {
        RestaurantCard(restaurant = previewRestaurant, onClick = {})
    }
}

@Preview(name = "Card – Closed – Light", showBackground = true, widthDp = 360)
@Composable
private fun RestaurantCardClosedLightPreview() {
    MunchiesTheme(darkTheme = false) {
        RestaurantCard(restaurant = previewRestaurant.copy(name = "Martin's Mancave", isOpen = false), onClick = {})
    }
}

@Preview(name = "Card – Open – Dark", showBackground = true, backgroundColor = 0xFF121212, widthDp = 360)
@Composable
private fun RestaurantCardOpenDarkPreview() {
    MunchiesTheme(darkTheme = true) {
        RestaurantCard(restaurant = previewRestaurant, onClick = {})
    }
}
