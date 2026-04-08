package se.scomas.munchies.shared.domain.mapper

import se.scomas.munchies.shared.domain.model.Filter
import se.scomas.munchies.shared.domain.model.OpenStatus
import se.scomas.munchies.shared.network.dto.FilterDto
import se.scomas.munchies.shared.network.dto.OpenStatusDto

fun FilterDto.toDomain() = Filter(
    id = id,
    name = name,
    imageUrl = imageUrl
)

fun OpenStatusDto.toDomain() = OpenStatus(
    restaurantId = restaurantId,
    isOpen = isCurrentlyOpen
)