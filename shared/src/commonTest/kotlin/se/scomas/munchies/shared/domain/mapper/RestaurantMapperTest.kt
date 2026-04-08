package se.scomas.munchies.shared.domain.mapper

import se.scomas.munchies.shared.network.dto.RestaurantDto
import kotlin.test.Test
import kotlin.test.assertEquals

class RestaurantMapperTest {

    private val dto = RestaurantDto(
        id = "7450001",
        name = "Wayne Burgers",
        rating = 4.6,
        deliveryTimeMinutes = 9,
        filterIds = listOf("5c64dea3-a4ac-4151-a2e3-42e7919a925d", "614fd642-3fa6-4f15-8786-dd3a8358cd78"),
        imageUrl = "https://food-delivery.umain.io/images/restaurant/burgers.png"
    )

    @Test
    fun `toDomain maps all fields correctly`() {
        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.name, domain.name)
        assertEquals(dto.rating, domain.rating)
        assertEquals(dto.deliveryTimeMinutes, domain.deliveryTimeMinutes)
        assertEquals(dto.filterIds, domain.filterIds)
        assertEquals(dto.imageUrl, domain.imageUrl)
    }

    @Test
    fun `toDomain preserves empty filterIds`() {
        val domain = dto.copy(filterIds = emptyList()).toDomain()
        assertEquals(emptyList(), domain.filterIds)
    }
}