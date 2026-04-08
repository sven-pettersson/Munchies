package se.scomas.munchies.shared.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantListDto(
    @SerialName("restaurants") val restaurants: List<RestaurantDto>
)

@Serializable
data class RestaurantDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("rating") val rating: Double,
    @SerialName("delivery_time_minutes") val deliveryTimeMinutes: Int,
    @SerialName("filterIds") val filterIds: List<String>,
    @SerialName("image_url") val imageUrl: String
)