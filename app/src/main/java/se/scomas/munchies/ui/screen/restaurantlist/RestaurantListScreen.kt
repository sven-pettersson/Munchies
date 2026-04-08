package se.scomas.munchies.ui.screen.restaurantlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import org.koin.androidx.compose.koinViewModel
import se.scomas.munchies.ui.screen.restaurantlist.components.FilterBar
import se.scomas.munchies.ui.screen.restaurantlist.components.RestaurantCard
import se.scomas.munchies.ui.screen.restaurantlist.components.RestaurantCardSkeleton
import se.scomas.munchies.ui.theme.MunchiesTheme
import se.scomas.munchies.ui.theme.munchiesColors
import se.scomas.munchies.ui.theme.spacing

// ── Stateful entry point ──────────────────────────────────────────────────────

@Composable
fun RestaurantListScreen(
    onRestaurantClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RestaurantListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.onRefresh()
        }
    }

    RestaurantListContent(
        uiState = uiState,
        onRestaurantClick = onRestaurantClick,
        onFilterClick = viewModel::onFilterToggle,
        onClearFilters = viewModel::onClearFilters,
        onRetry = { viewModel.onRefresh() },
        onRefresh = { viewModel.onRefresh() },
        modifier = modifier
    )
}

// ── Stateless content (previewable) ──────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RestaurantListContent(
    uiState: RestaurantListUiState,
    onRestaurantClick: (String) -> Unit,
    onFilterClick: (String) -> Unit,
    onClearFilters: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pull-to-refresh only active when content is already visible
    val isRefreshing = uiState.isLoading && uiState.restaurants.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.munchiesColors.background)
    ) {
        Text(
            text = "Munchies",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.munchiesColors.onBackground,
            modifier = Modifier.padding(
                start = MaterialTheme.spacing.md,
                end = MaterialTheme.spacing.md,
                top = MaterialTheme.spacing.lg,
                bottom = MaterialTheme.spacing.md
            )
        )

        if (uiState.filters.isNotEmpty()) {
            FilterBar(
                filters = uiState.filters,
                onFilterClick = onFilterClick,
                onClearFilters = onClearFilters,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(MaterialTheme.spacing.md))
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.isLoading && uiState.restaurants.isEmpty() -> SkeletonContent()
                uiState.error != null && uiState.restaurants.isEmpty() -> ErrorContent(
                    message = uiState.error,
                    onRetry = onRetry
                )
                uiState.restaurants.isEmpty() -> EmptyContent()
                else -> RestaurantList(
                    restaurants = uiState.restaurants,
                    onRestaurantClick = onRestaurantClick
                )
            }
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun RestaurantList(
    restaurants: List<RestaurantUiModel>,
    onRestaurantClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(MaterialTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        items(restaurants, key = { it.id }) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                onClick = { onRestaurantClick(restaurant.id) }
            )
        }
    }
}

@Composable
private fun SkeletonContent() {
    LazyColumn(
        contentPadding = PaddingValues(MaterialTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        userScrollEnabled = false
    ) {
        items(4) { RestaurantCardSkeleton() }
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

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No restaurants match your filters",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.munchiesColors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewFilters = listOf(
    FilterUiModel("1", "Top Rated",     "https://food-delivery.umain.io/images/filter/filter_top_rated.png",  isSelected = true),
    FilterUiModel("2", "Fast Delivery", "https://food-delivery.umain.io/images/filter/filter_fast_food.png",  isSelected = false),
    FilterUiModel("3", "Burgers",       "https://food-delivery.umain.io/images/filter/filter_burgers.png",    isSelected = false),
)

private val previewRestaurants = listOf(
    RestaurantUiModel("1", "Wayne Burgers",          4.6, 9,  "https://food-delivery.umain.io/images/restaurant/burgers.png",    isOpen = true),
    RestaurantUiModel("2", "Yuma's Candyshop",       4.7, 45, "https://food-delivery.umain.io/images/restaurant/candy.png",      isOpen = false),
    RestaurantUiModel("3", "Guillaume's Croissants", 5.0, 17, "https://food-delivery.umain.io/images/restaurant/croissants.png", isOpen = true),
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "List – Loaded – Light", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ListLoadedLightPreview() {
    MunchiesTheme(darkTheme = false) {
        RestaurantListContent(
            uiState = RestaurantListUiState(restaurants = previewRestaurants, filters = previewFilters),
            onRestaurantClick = {}, onFilterClick = {}, onClearFilters = {}, onRetry = {}, onRefresh = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "List – Loaded – Dark", showBackground = true, backgroundColor = 0xFF121212, widthDp = 360, heightDp = 800)
@Composable
private fun ListLoadedDarkPreview() {
    MunchiesTheme(darkTheme = true) {
        RestaurantListContent(
            uiState = RestaurantListUiState(restaurants = previewRestaurants, filters = previewFilters),
            onRestaurantClick = {}, onFilterClick = {}, onClearFilters = {}, onRetry = {}, onRefresh = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "List – Skeleton", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ListSkeletonPreview() {
    MunchiesTheme(darkTheme = false) {
        RestaurantListContent(
            uiState = RestaurantListUiState(isLoading = true),
            onRestaurantClick = {}, onFilterClick = {}, onClearFilters = {}, onRetry = {}, onRefresh = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "List – Error", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ListErrorPreview() {
    MunchiesTheme(darkTheme = false) {
        RestaurantListContent(
            uiState = RestaurantListUiState(error = "Could not reach the server"),
            onRestaurantClick = {}, onFilterClick = {}, onClearFilters = {}, onRetry = {}, onRefresh = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "List – Empty filters", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ListEmptyPreview() {
    MunchiesTheme(darkTheme = false) {
        RestaurantListContent(
            uiState = RestaurantListUiState(filters = previewFilters),
            onRestaurantClick = {}, onFilterClick = {}, onClearFilters = {}, onRetry = {}, onRefresh = {}
        )
    }
}