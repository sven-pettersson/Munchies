package se.scomas.munchies.shared.domain.usecase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import se.scomas.munchies.shared.domain.model.Restaurant

class FilterUseCase {

    private val _activeFilterIds = MutableStateFlow<Set<String>>(emptySet())
    val activeFilterIds: StateFlow<Set<String>> = _activeFilterIds.asStateFlow()

    /** Selects a filter if not active, deselects it if already active. */
    fun toggleFilter(filterId: String) {
        _activeFilterIds.update { current ->
            if (filterId in current) current - filterId else current + filterId
        }
    }

    fun clearFilters() {
        _activeFilterIds.value = emptySet()
    }

    /**
     * Returns restaurants matching any of the active filters.
     * If no filters are active the full list is returned unchanged.
     */
    fun filterRestaurants(
        restaurants: List<Restaurant>,
        activeFilterIds: Set<String>
    ): List<Restaurant> {
        if (activeFilterIds.isEmpty()) return restaurants
        return restaurants.filter { restaurant ->
            restaurant.filterIds.any { it in activeFilterIds }
        }
    }
}
