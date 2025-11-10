package com.example.fooddeliveryapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * API response model for restaurant data
 */
data class RestaurantResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("distance")
    val distance: Double
)

/**
 * API response wrapper for restaurant list
 */
data class RestaurantsApiResponse(
    @SerializedName("data")
    val data: List<RestaurantResponse>
)

/**
 * Maps API response to domain model
 */
fun RestaurantResponse.toDomainModel(): Restaurant {
    return Restaurant(
        id = id,
        name = name,
        description = address, // Using address as description
        imageUrl = imageUrl,
        rating = 4.5f, // Default rating, should come from reviews API later
        deliveryTime = "20-30 min", // Default delivery time
        deliveryFee = 2.99, // Default delivery fee
        categories = listOf(categoryId), // Using categoryId, can be mapped to category names later
        isOpen = true, // Default to open
        distance = String.format("%.1f km", distance * 111.0) // Convert degrees to km (approximate)
    )
}
