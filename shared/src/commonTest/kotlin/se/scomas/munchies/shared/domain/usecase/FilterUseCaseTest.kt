package se.scomas.munchies.shared.domain.usecase

import se.scomas.munchies.shared.domain.model.Restaurant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilterUseCaseTest {

    private val useCase = FilterUseCase()

    private val restaurants = listOf(
        Restaurant("1", "Burgers",  4.6, 9,  listOf("top-rated", "fast"),  ""),
        Restaurant("2", "Candy",    4.7, 45, listOf("top-rated"),           ""),
        Restaurant("3", "Pizza",    4.4, 3,  listOf("fast"),                ""),
        Restaurant("4", "Coffee",   0.7, 12, listOf("coffee"),              ""),
        Restaurant("5", "Yogurt",   1.2, 45, emptyList(),                   "")
    )

    // ── Toggle ────────────────────────────────────────────────────────────────

    @Test
    fun `toggleFilter adds filter when not active`() {
        useCase.toggleFilter("top-rated")
        assertTrue("top-rated" in useCase.activeFilterIds.value)
    }

    @Test
    fun `toggleFilter removes filter when already active`() {
        useCase.toggleFilter("top-rated")
        useCase.toggleFilter("top-rated")
        assertFalse("top-rated" in useCase.activeFilterIds.value)
    }

    @Test
    fun `toggleFilter supports multiple active filters`() {
        useCase.toggleFilter("top-rated")
        useCase.toggleFilter("fast")
        assertEquals(setOf("top-rated", "fast"), useCase.activeFilterIds.value)
    }

    // ── Clear ─────────────────────────────────────────────────────────────────

    @Test
    fun `clearFilters resets active set to empty`() {
        useCase.toggleFilter("top-rated")
        useCase.toggleFilter("fast")
        useCase.clearFilters()
        assertTrue(useCase.activeFilterIds.value.isEmpty())
    }

    // ── Filter logic ──────────────────────────────────────────────────────────

    @Test
    fun `filterRestaurants returns all when no filters active`() {
        val result = useCase.filterRestaurants(restaurants, emptySet())
        assertEquals(5, result.size)
    }

    @Test
    fun `filterRestaurants matches any active filter (OR logic)`() {
        // "top-rated" matches Burgers + Candy; "fast" matches Burgers + Pizza
        val result = useCase.filterRestaurants(restaurants, setOf("top-rated", "fast"))
        assertEquals(3, result.size)
        assertTrue(result.any { it.id == "1" }) // Burgers — matches both
        assertTrue(result.any { it.id == "2" }) // Candy — matches top-rated
        assertTrue(result.any { it.id == "3" }) // Pizza — matches fast
    }

    @Test
    fun `filterRestaurants single filter returns only matching restaurants`() {
        val result = useCase.filterRestaurants(restaurants, setOf("coffee"))
        assertEquals(1, result.size)
        assertEquals("4", result[0].id)
    }

    @Test
    fun `filterRestaurants excludes restaurants with no filterIds`() {
        val result = useCase.filterRestaurants(restaurants, setOf("top-rated"))
        assertFalse(result.any { it.id == "5" }) // Yogurt has no filterIds
    }

    @Test
    fun `filterRestaurants returns empty list when no match`() {
        val result = useCase.filterRestaurants(restaurants, setOf("nonexistent-filter"))
        assertTrue(result.isEmpty())
    }
}