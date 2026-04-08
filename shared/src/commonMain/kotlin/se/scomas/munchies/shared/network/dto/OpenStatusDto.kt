package se.scomas.munchies.shared.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenStatusDto(
    @SerialName("restaurant_id") val restaurantId: String,
    @SerialName("is_currently_open") val isCurrentlyOpen: Boolean
)