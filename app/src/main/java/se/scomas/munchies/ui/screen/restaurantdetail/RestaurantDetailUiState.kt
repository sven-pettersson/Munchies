package se.scomas.munchies.ui.screen.restaurantdetail

data class RestaurantDetailUiState(
    val restaurant: RestaurantDetailUiModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class RestaurantDetailUiModel(
    val id: String,
    val name: String,
    val rating: Double,
    val deliveryTimeMinutes: Int,
    val imageUrl: String,
    val isOpen: Boolean?,
    val filters: List<FilterTagUiModel>
)

data class FilterTagUiModel(
    val id: String,
    val name: String,
    val imageUrl: String
)