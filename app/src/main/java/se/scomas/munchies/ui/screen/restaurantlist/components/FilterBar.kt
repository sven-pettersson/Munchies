package se.scomas.munchies.ui.screen.restaurantlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import se.scomas.munchies.R
import se.scomas.munchies.ui.screen.restaurantlist.FilterUiModel
import se.scomas.munchies.ui.theme.MunchiesTheme
import se.scomas.munchies.ui.theme.munchiesColors
import se.scomas.munchies.ui.theme.radius
import se.scomas.munchies.ui.theme.spacing

@Composable
fun FilterBar(
    filters: List<FilterUiModel>,
    onFilterClick: (String) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasActiveFilters = filters.any { it.isSelected }

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
    ) {
        // Clear chip — only visible when at least one filter is active
        if (hasActiveFilters) {
            item(key = "clear") {
                ClearChip(onClick = onClearFilters)
            }
        }
        items(filters, key = { it.id }) { filter ->
            FilterChip(filter = filter, onClick = { onFilterClick(filter.id) })
        }
    }
}

@Composable
private fun ClearChip(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.radius.chip)
            .background(MaterialTheme.munchiesColors.closedRed.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(
                horizontal = MaterialTheme.spacing.sm,
                vertical = MaterialTheme.spacing.xs
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = "Clear filters",
            tint = MaterialTheme.munchiesColors.closedRed,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(MaterialTheme.spacing.xs))
        Text(
            text = "Clear",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.munchiesColors.closedRed
        )
    }
}

@Composable
private fun FilterChip(
    filter: FilterUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.munchiesColors
    val chipShape = MaterialTheme.radius.chip

    val backgroundColor = if (filter.isSelected) colors.chipSelectedBackground
                          else colors.chipUnselectedBackground
    val contentColor = if (filter.isSelected) colors.chipSelectedContent
                       else colors.chipUnselectedContent

    Row(
        modifier = modifier
            .clip(chipShape)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(
                horizontal = MaterialTheme.spacing.sm,
                vertical = MaterialTheme.spacing.xs
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = filter.imageUrl,
            contentDescription = filter.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(24.dp)
                .clip(MaterialTheme.radius.chip)
        )
        Spacer(Modifier.width(MaterialTheme.spacing.xs))
        Text(
            text = filter.name,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewFilters = listOf(
    FilterUiModel("1", "Top Rated",     "https://food-delivery.umain.io/images/filter/filter_top_rated.png",  isSelected = true),
    FilterUiModel("2", "Fast Delivery", "https://food-delivery.umain.io/images/filter/filter_fast_food.png",  isSelected = false),
    FilterUiModel("3", "Burgers",       "https://food-delivery.umain.io/images/filter/filter_burgers.png",    isSelected = false),
    FilterUiModel("4", "Asian",         "https://food-delivery.umain.io/images/filter/filter_asian.png",      isSelected = true),
)

@Preview(name = "Filter Bar – With active filters", showBackground = true)
@Composable
private fun FilterBarActivePreview() {
    MunchiesTheme(darkTheme = false) {
        FilterBar(filters = previewFilters, onFilterClick = {}, onClearFilters = {})
    }
}

@Preview(name = "Filter Bar – No active filters", showBackground = true)
@Composable
private fun FilterBarNoActivePreview() {
    MunchiesTheme(darkTheme = false) {
        FilterBar(
            filters = previewFilters.map { it.copy(isSelected = false) },
            onFilterClick = {},
            onClearFilters = {}
        )
    }
}

@Preview(name = "Filter Bar – Dark", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun FilterBarDarkPreview() {
    MunchiesTheme(darkTheme = true) {
        FilterBar(filters = previewFilters, onFilterClick = {}, onClearFilters = {})
    }
}