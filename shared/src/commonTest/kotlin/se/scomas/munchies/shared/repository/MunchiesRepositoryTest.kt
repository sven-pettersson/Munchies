package se.scomas.munchies.shared.repository

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import se.scomas.munchies.shared.domain.model.Filter
import se.scomas.munchies.shared.domain.model.OpenStatus
import se.scomas.munchies.shared.domain.model.Restaurant
import se.scomas.munchies.shared.network.MunchiesApi
import se.scomas.munchies.shared.util.Result
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MunchiesRepositoryTest {

    // ── Fake API ──────────────────────────────────────────────────────────────

    private val fakeRestaurants = listOf(
        Restaurant("7450001", "Wayne Burgers", 4.6, 9, listOf("filter-1"), "https://img.png"),
        Restaurant("7450002", "Yuma Candyshop", 4.7, 45, listOf("filter-1", "filter-2"), "https://img2.png")
    )

    private val fakeFilters = mapOf(
        "filter-1" to Filter("filter-1", "Top Rated", "https://filter1.png"),
        "filter-2" to Filter("filter-2", "Fast Delivery", "https://filter2.png")
    )

    private val fakeOpenStatuses = mapOf(
        "7450001" to OpenStatus("7450001", isOpen = true),
        "7450002" to OpenStatus("7450002", isOpen = false)
    )

    private fun successApi() = object : MunchiesApi {
        override suspend fun getRestaurants() = Result.Success(fakeRestaurants)
        override suspend fun getFilter(filterId: String) =
            Result.Success(fakeFilters[filterId] ?: error("Unknown filter $filterId"))
        override suspend fun getOpenStatus(restaurantId: String) =
            Result.Success(fakeOpenStatuses[restaurantId] ?: error("Unknown restaurant $restaurantId"))
    }

    private fun errorApi() = object : MunchiesApi {
        override suspend fun getRestaurants() = Result.Error("Network failure")
        override suspend fun getFilter(filterId: String) = Result.Error("Network failure")
        override suspend fun getOpenStatus(restaurantId: String) = Result.Error("Network failure")
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    fun `refresh populates restaurants flow`() = runTest {
        val repo = MunchiesRepositoryImpl(api = successApi(), scope = this)
        testScheduler.advanceUntilIdle()

        assertEquals(2, repo.restaurants.value.size)
        assertEquals("Wayne Burgers", repo.restaurants.value[0].name)
    }

    @Test
    fun `refresh resolves and deduplicates filters`() = runTest {
        val repo = MunchiesRepositoryImpl(api = successApi(), scope = this)
        testScheduler.advanceUntilIdle()

        // filter-1 appears on both restaurants — should only appear once
        assertEquals(2, repo.filters.value.size)
        assertTrue(repo.filters.value.any { it.id == "filter-1" })
        assertTrue(repo.filters.value.any { it.id == "filter-2" })
    }

    @Test
    fun `refresh fetches open status for all restaurants`() = runTest {
        val repo = MunchiesRepositoryImpl(api = successApi(), scope = this)
        testScheduler.advanceUntilIdle()

        assertEquals(2, repo.openStatuses.value.size)
        assertTrue(repo.openStatuses.value["7450001"]?.isOpen == true)
        assertFalse(repo.openStatuses.value["7450002"]?.isOpen == true)
    }

    @Test
    fun `refresh sets error on network failure`() = runTest {
        val repo = MunchiesRepositoryImpl(api = errorApi(), scope = this)
        testScheduler.advanceUntilIdle()

        assertNotNull(repo.error.value)
        assertEquals("Network failure", repo.error.value)
        assertTrue(repo.restaurants.value.isEmpty())
    }

    @Test
    fun `isLoading is false after refresh completes`() = runTest {
        val repo = MunchiesRepositoryImpl(api = successApi(), scope = this)
        testScheduler.advanceUntilIdle()

        assertFalse(repo.isLoading.value)
    }

    @Test
    fun `second refresh updates data`() = runTest {
        val repo = MunchiesRepositoryImpl(api = successApi(), scope = this)
        testScheduler.advanceUntilIdle()

        repo.refresh()
        testScheduler.advanceUntilIdle()

        assertEquals(2, repo.restaurants.value.size)
        assertNull(repo.error.value)
    }
}