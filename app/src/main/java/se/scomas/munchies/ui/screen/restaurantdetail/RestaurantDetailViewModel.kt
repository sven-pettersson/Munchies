package se.scomas.munchies.ui.screen.restaurantdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import se.scomas.munchies.shared.repository.MunchiesRepository

class RestaurantDetailViewModel(
    savedStateHandle: SavedStateHandle,
    repository: MunchiesRepository
) : ViewModel() {

    private val restaurantId: String = checkNotNull(savedStateHandle["restaurantId"])

    val uiState = combine(
        repository.restaurants,
        repository.filters,
        repository.openStatuses,
        repository.isLoading
    ) { restaurants, filters, openStatuses, isLoading ->
        val restaurant = restaurants.find { it.id == restaurantId }
        when {
            isLoading && restaurant == null -> RestaurantDetailUiState(isLoading = true)
            restaurant == null -> RestaurantDetailUiState(error = "Restaurant not found")
            else -> RestaurantDetailUiState(
                restaurant = RestaurantDetailUiModel(
                    id = restaurant.id,
                    name = restaurant.name,
                    rating = restaurant.rating,
                    deliveryTimeMinutes = restaurant.deliveryTimeMinutes,
                    imageUrl = restaurant.imageUrl,
                    isOpen = openStatuses[restaurantId]?.isOpen,
                    filters = filters
                        .filter { it.id in restaurant.filterIds }
                        .map { FilterTagUiModel(it.id, it.name, it.imageUrl) }
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RestaurantDetailUiState(isLoading = true)
    )
}