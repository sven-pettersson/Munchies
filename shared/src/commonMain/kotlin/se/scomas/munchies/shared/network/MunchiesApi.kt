package se.scomas.munchies.shared.network

import se.scomas.munchies.shared.domain.model.Filter
import se.scomas.munchies.shared.domain.model.OpenStatus
import se.scomas.munchies.shared.domain.model.Restaurant
import se.scomas.munchies.shared.util.Result

interface MunchiesApi {
    suspend fun getRestaurants(): Result<List<Restaurant>>
    suspend fun getFilter(filterId: String): Result<Filter>
    suspend fun getOpenStatus(restaurantId: String): Result<OpenStatus>
}