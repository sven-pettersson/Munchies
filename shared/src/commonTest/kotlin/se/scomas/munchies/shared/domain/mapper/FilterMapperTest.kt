package se.scomas.munchies.shared.domain.mapper

import se.scomas.munchies.shared.network.dto.FilterDto
import se.scomas.munchies.shared.network.dto.OpenStatusDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilterMapperTest {

    @Test
    fun `FilterDto toDomain maps all fields correctly`() {
        val dto = FilterDto(
            id = "5c64dea3-a4ac-4151-a2e3-42e7919a925d",
            name = "Top Rated",
            imageUrl = "https://food-delivery.umain.io/images/filter/filter_top_rated.png"
        )

        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.name, domain.name)
        assertEquals(dto.imageUrl, domain.imageUrl)
    }

    @Test
    fun `OpenStatusDto toDomain maps isCurrentlyOpen true correctly`() {
        val dto = OpenStatusDto(restaurantId = "7450001", isCurrentlyOpen = true)
        val domain = dto.toDomain()

        assertEquals("7450001", domain.restaurantId)
        assertTrue(domain.isOpen)
    }

    @Test
    fun `OpenStatusDto toDomain maps isCurrentlyOpen false correctly`() {
        val dto = OpenStatusDto(restaurantId = "7450002", isCurrentlyOpen = false)
        val domain = dto.toDomain()

        assertFalse(domain.isOpen)
    }
}