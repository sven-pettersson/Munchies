package se.scomas.munchies.ui.screen.restaurantlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import se.scomas.munchies.shared.domain.model.Filter
import se.scomas.munchies.shared.domain.model.OpenStatus
import se.scomas.munchies.shared.domain.model.Restaurant
import se.scomas.munchies.shared.domain.usecase.FilterUseCase
import se.scomas.munchies.shared.repository.MunchiesRepository

class RestaurantListViewModel(
    private val repository: MunchiesRepository,
    private val filterUseCase: FilterUseCase
) : ViewModel() {

    val uiState = combine(
        combine(
            repository.restaurants,
            repository.filters,
            repository.openStatuses,
            repository.isLoading,
            repository.error,
        ) { restaurants, filters, openStatuses, isLoading, error ->
            RepoSnapshot(restaurants, filters, openStatuses, isLoading, error)
        },
        filterUseCase.activeFilterIds
    ) { snapshot, activeFilterIds ->
        val filtered = filterUseCase.filterRestaurants(snapshot.restaurants, activeFilterIds)
        RestaurantListUiState(
            restaurants = filtered.map { it.toUiModel(snapshot.openStatuses) },
            filters = snapshot.filters.map { it.toUiModel(activeFilterIds) },
            isLoading = snapshot.isLoading,
            error = snapshot.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RestaurantListUiState(isLoading = true)
    )

    fun onFilterToggle(filterId: String) = filterUseCase.toggleFilter(filterId)
    fun onClearFilters() = filterUseCase.clearFilters()
    fun onRefresh() = viewModelScope.launch { repository.refresh() }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun Restaurant.toUiModel(openStatuses: Map<String, OpenStatus>) = RestaurantUiModel(
        id = id,
        name = name,
        rating = rating,
        deliveryTimeMinutes = deliveryTimeMinutes,
        imageUrl = imageUrl,
        isOpen = openStatuses[id]?.isOpen
    )

    private fun Filter.toUiModel(activeFilterIds: Set<String>) = FilterUiModel(
        id = id,
        name = name,
        imageUrl = imageUrl,
        isSelected = id in activeFilterIds
    )

    private data class RepoSnapshot(
        val restaurants: List<Restaurant>,
        val filters: List<Filter>,
        val openStatuses: Map<String, OpenStatus>,
        val isLoading: Boolean,
        val error: String?
    )
}