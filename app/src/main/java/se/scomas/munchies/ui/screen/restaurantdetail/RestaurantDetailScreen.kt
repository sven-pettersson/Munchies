package se.scomas.munchies.ui.screen.restaurantdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import se.scomas.munchies.R
import se.scomas.munchies.ui.theme.MunchiesTheme
import se.scomas.munchies.ui.theme.munchiesColors
import se.scomas.munchies.ui.theme.radius
import se.scomas.munchies.ui.theme.spacing

// ── Stateful entry point ──────────────────────────────────────────────────────

@Composable
fun RestaurantDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RestaurantDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    RestaurantDetailContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = { viewModel.onRetry() },
        modifier = modifier
    )
}

// ── Stateless content (previewable) ──────────────────────────────────────────

@Composable
internal fun RestaurantDetailContent(
    uiState: RestaurantDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.munchiesColors.background)
    ) {
        when {
            uiState.isLoading -> LoadingContent()
            uiState.error != null -> ErrorContent(message = uiState.error, onRetry = onRetry)
            uiState.restaurant != null -> DetailContent(
                restaurant = uiState.restaurant,
                onBack = onBack
            )
        }
    }
}

// ── Detail layout ─────────────────────────────────────────────────────────────

@Composable
private fun DetailContent(
    restaurant: RestaurantDetailUiModel,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        // ── Hero image with floating back button ──────────────────────────────
        Box {
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
            )
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(MaterialTheme.spacing.sm)
                    .align(Alignment.TopStart)
                    .clip(MaterialTheme.radius.chip)
                    .background(Color.Black.copy(alpha = 0.35f))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Open / closed badge on image
            restaurant.isOpen?.let { isOpen ->
                val badgeColor = if (isOpen) MaterialTheme.munchiesColors.openGreen
                                 else MaterialTheme.munchiesColors.closedRed
                val label = if (isOpen) "Open now" else "Closed"
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(MaterialTheme.spacing.md)
                        .clip(MaterialTheme.radius.badge)
                        .background(badgeColor)
                        .padding(
                            horizontal = MaterialTheme.spacing.sm,
                            vertical = MaterialTheme.spacing.xs
                        )
                )
            }
        }

        // ── Info section ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.md,
                vertical = MaterialTheme.spacing.lg
            )
        ) {
            Text(
                text = restaurant.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.munchiesColors.onBackground
            )

            Spacer(Modifier.height(MaterialTheme.spacing.sm))

            // Rating + delivery time
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetaItem(
                    icon = R.drawable.ic_star,
                    iconTint = MaterialTheme.munchiesColors.starYellow,
                    label = String.format("%.1f", restaurant.rating)
                )
                MetaItem(
                    icon = R.drawable.ic_clock,
                    iconTint = MaterialTheme.munchiesColors.onSurfaceVariant,
                    label = "${restaurant.deliveryTimeMinutes} min"
                )
            }

            // Filter tags
            if (restaurant.filters.isNotEmpty()) {
                Spacer(Modifier.height(MaterialTheme.spacing.lg))
                HorizontalDivider(color = MaterialTheme.munchiesColors.outline)
                Spacer(Modifier.height(MaterialTheme.spacing.lg))

                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.munchiesColors.onSurfaceVariant
                )
                Spacer(Modifier.height(MaterialTheme.spacing.sm))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                    items(restaurant.filters, key = { it.id }) { filter ->
                        FilterTag(filter = filter)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaItem(icon: Int, iconTint: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(MaterialTheme.spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.munchiesColors.onSurfaceVariant
        )
    }
}

@Composable
private fun FilterTag(filter: FilterTagUiModel) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.radius.chip)
            .background(MaterialTheme.munchiesColors.surfaceVariant)
            .padding(
                horizontal = MaterialTheme.spacing.sm,
                vertical = MaterialTheme.spacing.xs
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = filter.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(20.dp)
                .clip(MaterialTheme.radius.chip)
        )
        Spacer(Modifier.width(MaterialTheme.spacing.xs))
        Text(
            text = filter.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.munchiesColors.onSurface
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.munchiesColors.onBackground)
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.munchiesColors.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(MaterialTheme.spacing.sm))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.munchiesColors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(MaterialTheme.spacing.lg))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewRestaurant = RestaurantDetailUiModel(
    id = "1",
    name = "Wayne \"Chad Broski\" Burgers",
    rating = 4.6,
    deliveryTimeMinutes = 9,
    imageUrl = "https://food-delivery.umain.io/images/restaurant/burgers.png",
    isOpen = true,
    filters = listOf(
        FilterTagUiModel("1", "Top Rated",     "https://food-delivery.umain.io/images/filter/filter_top_rated.png"),
        FilterTagUiModel("2", "Fast Delivery", "https://food-delivery.umain.io/images/filter/filter_fast_food.png"),
    )
)

@Preview(name = "Detail – Open – Light", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun DetailOpenLightPreview() {
    MunchiesTheme(darkTheme = false) {
        RestaurantDetailContent(
            uiState = RestaurantDetailUiState(restaurant = previewRestaurant),
            onBack = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Detail – Closed – Dark", showBackground = true, backgroundColor = 0xFF121212, widthDp = 360, heightDp = 800)
@Composable
private fun DetailClosedDarkPreview() {
    MunchiesTheme(darkTheme = true) {
        RestaurantDetailContent(
            uiState = RestaurantDetailUiState(restaurant = previewRestaurant.copy(isOpen = false)),
            onBack = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Detail – Loading", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun DetailLoadingPreview() {
    MunchiesTheme(darkTheme = false) {
        RestaurantDetailContent(
            uiState = RestaurantDetailUiState(isLoading = true),
            onBack = {},
            onRetry = {}
        )
    }
}