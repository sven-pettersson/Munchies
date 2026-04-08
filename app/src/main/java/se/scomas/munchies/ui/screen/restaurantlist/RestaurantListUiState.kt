package se.scomas.munchies.ui.screen.restaurantlist

data class RestaurantListUiState(
    val restaurants: List<RestaurantUiModel> = emptyList(),
    val filters: List<FilterUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class RestaurantUiModel(
    val id: String,
    val name: String,
    val rating: Double,
    val deliveryTimeMinutes: Int,
    val imageUrl: String,
    val isOpen: Boolean?   // null = status not yet loaded
)

data class FilterUiModel(
    val id: String,
    val name: String,
    val imageUrl: String,
    val isSelected: Boolean
)
