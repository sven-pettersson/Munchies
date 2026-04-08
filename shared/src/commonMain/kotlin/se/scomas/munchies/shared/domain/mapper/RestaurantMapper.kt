package se.scomas.munchies.shared.domain.mapper

import se.scomas.munchies.shared.domain.model.Restaurant
import se.scomas.munchies.shared.network.dto.RestaurantDto

fun RestaurantDto.toDomain() = Restaurant(
    id = id,
    name = name,
    rating = rating,
    deliveryTimeMinutes = deliveryTimeMinutes,
    filterIds = filterIds,
    imageUrl = imageUrl
)