package se.scomas.munchies.shared.domain.model

data class Restaurant(
    val id: String,
    val name: String,
    val rating: Double,
    val deliveryTimeMinutes: Int,
    val filterIds: List<String>,
    val imageUrl: String
)