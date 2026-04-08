package se.scomas.munchies.shared.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import se.scomas.munchies.shared.domain.model.Filter
import se.scomas.munchies.shared.domain.model.OpenStatus
import se.scomas.munchies.shared.domain.model.Restaurant
import se.scomas.munchies.shared.network.MunchiesApi
import se.scomas.munchies.shared.util.Result

class MunchiesRepositoryImpl(
    private val api: MunchiesApi,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) : MunchiesRepository {

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    override val restaurants: StateFlow<List<Restaurant>> = _restaurants.asStateFlow()

    private val _filters = MutableStateFlow<List<Filter>>(emptyList())
    override val filters: StateFlow<List<Filter>> = _filters.asStateFlow()

    private val _openStatuses = MutableStateFlow<Map<String, OpenStatus>>(emptyMap())
    override val openStatuses: StateFlow<Map<String, OpenStatus>> = _openStatuses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    private val refreshMutex = Mutex()

    init {
        // Populate cache as soon as the repository is created.
        scope.launch { refresh() }
    }

    override suspend fun refresh() {
        // If a refresh is already in progress, skip — don't queue up a second one.
        if (refreshMutex.isLocked) return
        refreshMutex.withLock {
            _isLoading.value = true
            _error.value = null

            when (val result = api.getRestaurants()) {
                is Result.Success -> {
                    val restaurantList = result.data
                    _restaurants.value = restaurantList

                    // Fetch filters and open statuses concurrently.
                    coroutineScope {
                        val filterIds = restaurantList.flatMap { it.filterIds }.toSet()

                        val filtersDeferred = filterIds.map { id ->
                            async { api.getFilter(id) }
                        }
                        val statusesDeferred = restaurantList.map { restaurant ->
                            async { api.getOpenStatus(restaurant.id) }
                        }

                        _filters.value = filtersDeferred.awaitAll()
                            .filterIsInstance<Result.Success<Filter>>()
                            .map { it.data }

                        _openStatuses.value = statusesDeferred.awaitAll()
                            .filterIsInstance<Result.Success<OpenStatus>>()
                            .map { it.data }
                            .associateBy { it.restaurantId }
                    }
                }

                is Result.Error -> _error.value = result.message
            }

            _isLoading.value = false
        }
    }
}