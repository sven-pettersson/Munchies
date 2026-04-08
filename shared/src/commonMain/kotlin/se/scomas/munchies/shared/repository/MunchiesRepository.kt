package se.scomas.munchies.shared.repository

import kotlinx.coroutines.flow.StateFlow
import se.scomas.munchies.shared.domain.model.Filter
import se.scomas.munchies.shared.domain.model.OpenStatus
import se.scomas.munchies.shared.domain.model.Restaurant

interface MunchiesRepository {
    val restaurants: StateFlow<List<Restaurant>>
    val filters: StateFlow<List<Filter>>
    val openStatuses: StateFlow<Map<String, OpenStatus>>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    /** Fetches fresh data from the network and updates all flows. Safe to call concurrently. */
    suspend fun refresh()
}
