package se.scomas.munchies.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import se.scomas.munchies.shared.domain.mapper.toDomain
import se.scomas.munchies.shared.domain.model.Filter
import se.scomas.munchies.shared.domain.model.OpenStatus
import se.scomas.munchies.shared.domain.model.Restaurant
import se.scomas.munchies.shared.network.dto.FilterDto
import se.scomas.munchies.shared.network.dto.OpenStatusDto
import se.scomas.munchies.shared.network.dto.RestaurantListDto
import se.scomas.munchies.shared.util.Result

class MunchiesApiImpl(private val client: HttpClient) : MunchiesApi {

    override suspend fun getRestaurants(): Result<List<Restaurant>> = safeCall {
        client.get("$BASE_URL/restaurants").body<RestaurantListDto>().restaurants.map { it.toDomain() }
    }

    override suspend fun getFilter(filterId: String): Result<Filter> = safeCall {
        client.get("$BASE_URL/filter/$filterId").body<FilterDto>().toDomain()
    }

    override suspend fun getOpenStatus(restaurantId: String): Result<OpenStatus> = safeCall {
        client.get("$BASE_URL/open/$restaurantId").body<OpenStatusDto>().toDomain()
    }

    private suspend fun <T> safeCall(call: suspend () -> T): Result<T> = try {
        Result.Success(call())
    } catch (e: Exception) {
        Result.Error(e.message ?: "Unknown network error", e)
    }

    companion object {
        private const val BASE_URL = "https://food-delivery.umain.io/api/v1"
    }
}