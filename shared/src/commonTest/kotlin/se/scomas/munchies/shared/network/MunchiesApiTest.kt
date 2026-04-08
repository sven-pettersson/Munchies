package se.scomas.munchies.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import se.scomas.munchies.shared.util.Result
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MunchiesApiTest {

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun buildClient(engine: MockEngine): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val jsonHeaders = headersOf("Content-Type", ContentType.Application.Json.toString())

    // ── Restaurant list ───────────────────────────────────────────────────────

    @Test
    fun `getRestaurants returns list on success`() = runTest {
        val engine = MockEngine { request ->
            assertTrue(request.url.encodedPath.endsWith("/restaurants"))
            respond(RESTAURANTS_JSON, HttpStatusCode.OK, jsonHeaders)
        }
        val api = MunchiesApiImpl(buildClient(engine))

        val result = api.getRestaurants()

        assertIs<Result.Success<*>>(result)
        val restaurants = (result as Result.Success).data
        assertEquals(2, restaurants.size)
        assertEquals("7450001", restaurants[0].id)
        assertEquals("Wayne Burgers", restaurants[0].name)
        assertEquals(4.6, restaurants[0].rating)
        assertEquals(9, restaurants[0].deliveryTimeMinutes)
        assertEquals(2, restaurants[0].filterIds.size)
    }

    @Test
    fun `getRestaurants returns error on server error`() = runTest {
        val engine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
        val api = MunchiesApiImpl(buildClient(engine))

        val result = api.getRestaurants()

        assertIs<Result.Error>(result)
    }

    @Test
    fun `getRestaurants returns error on malformed JSON`() = runTest {
        val engine = MockEngine { respond("not json", HttpStatusCode.OK, jsonHeaders) }
        val api = MunchiesApiImpl(buildClient(engine))

        val result = api.getRestaurants()

        assertIs<Result.Error>(result)
    }

    // ── Filter ────────────────────────────────────────────────────────────────

    @Test
    fun `getFilter returns filter on success`() = runTest {
        val filterId = "5c64dea3-a4ac-4151-a2e3-42e7919a925d"
        val engine = MockEngine { request ->
            assertTrue(request.url.encodedPath.endsWith("/filter/$filterId"))
            respond(FILTER_JSON, HttpStatusCode.OK, jsonHeaders)
        }
        val api = MunchiesApiImpl(buildClient(engine))

        val result = api.getFilter(filterId)

        assertIs<Result.Success<*>>(result)
        val filter = (result as Result.Success).data
        assertEquals(filterId, filter.id)
        assertEquals("Top Rated", filter.name)
        assertTrue(filter.imageUrl.isNotEmpty())
    }

    @Test
    fun `getFilter returns error on 404`() = runTest {
        val engine = MockEngine { respondError(HttpStatusCode.NotFound) }
        val api = MunchiesApiImpl(buildClient(engine))

        val result = api.getFilter("unknown-id")

        assertIs<Result.Error>(result)
    }

    // ── Open status ───────────────────────────────────────────────────────────

    @Test
    fun `getOpenStatus returns open true on success`() = runTest {
        val restaurantId = "7450001"
        val engine = MockEngine { request ->
            assertTrue(request.url.encodedPath.endsWith("/open/$restaurantId"))
            respond(OPEN_STATUS_JSON, HttpStatusCode.OK, jsonHeaders)
        }
        val api = MunchiesApiImpl(buildClient(engine))

        val result = api.getOpenStatus(restaurantId)

        assertIs<Result.Success<*>>(result)
        val status = (result as Result.Success).data
        assertEquals(restaurantId, status.restaurantId)
        assertTrue(status.isOpen)
    }

    @Test
    fun `getOpenStatus returns error on server error`() = runTest {
        val engine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
        val api = MunchiesApiImpl(buildClient(engine))

        val result = api.getOpenStatus("7450001")

        assertIs<Result.Error>(result)
    }

    // ── Test fixtures ─────────────────────────────────────────────────────────

    companion object {
        private val RESTAURANTS_JSON = """
            {
              "restaurants": [
                {
                  "id": "7450001",
                  "name": "Wayne Burgers",
                  "rating": 4.6,
                  "delivery_time_minutes": 9,
                  "filterIds": ["5c64dea3-a4ac-4151-a2e3-42e7919a925d", "614fd642-3fa6-4f15-8786-dd3a8358cd78"],
                  "image_url": "https://food-delivery.umain.io/images/restaurant/burgers.png"
                },
                {
                  "id": "7450002",
                  "name": "Yuma Candyshop",
                  "rating": 4.7,
                  "delivery_time_minutes": 45,
                  "filterIds": ["5c64dea3-a4ac-4151-a2e3-42e7919a925d"],
                  "image_url": "https://food-delivery.umain.io/images/restaurant/candy.png"
                }
              ]
            }
        """.trimIndent()

        private val FILTER_JSON = """
            {
              "id": "5c64dea3-a4ac-4151-a2e3-42e7919a925d",
              "name": "Top Rated",
              "image_url": "https://food-delivery.umain.io/images/filter/filter_top_rated.png"
            }
        """.trimIndent()

        private val OPEN_STATUS_JSON = """
            {
              "restaurant_id": "7450001",
              "is_currently_open": true
            }
        """.trimIndent()
    }
}