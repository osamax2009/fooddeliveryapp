package com.example.fooddeliveryapp.data.api

import com.example.fooddeliveryapp.data.model.OrderApiResponse
import com.example.fooddeliveryapp.data.model.OrdersApiResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * API service for Rider-specific endpoints
 */
interface RiderApiService {

    /**
     * Get all available delivery requests
     */
    @GET("rider/deliveries/available")
    suspend fun getAvailableDeliveries(): Response<OrdersApiResponse>

    /**
     * Get rider's active/assigned deliveries
     */
    @GET("rider/deliveries/active")
    suspend fun getActiveDeliveries(): Response<OrdersApiResponse>

    /**
     * Accept a delivery request
     * @param orderId The order ID to accept
     */
    @POST("rider/deliveries/{orderId}/accept")
    suspend fun acceptDelivery(
        @Path("orderId") orderId: String
    ): Response<OrderApiResponse>

    /**
     * Reject a delivery request
     * @param orderId The order ID to reject
     */
    @POST("rider/deliveries/{orderId}/reject")
    suspend fun rejectDelivery(
        @Path("orderId") orderId: String
    ): Response<OrderApiResponse>

    /**
     * Update delivery status (PICKED_UP, DELIVERED, FAILED)
     * @param orderId The order ID
     * @param request Status update request with status and optional reason
     */
    @POST("rider/deliveries/{orderId}/status")
    suspend fun updateDeliveryStatus(
        @Path("orderId") orderId: String,
        @Body request: UpdateDeliveryStatusRequest
    ): Response<OrderApiResponse>

    /**
     * Get navigation path for a delivery
     * @param orderId The order ID
     */
    @GET("rider/deliveries/{orderId}/path")
    suspend fun getDeliveryPath(
        @Path("orderId") orderId: String
    ): Response<DeliveryPathResponse>

    /**
     * Update rider's current location
     * @param request Location update request with latitude, longitude, and address
     */
    @POST("rider/location")
    suspend fun updateLocation(
        @Body request: UpdateLocationRequest
    ): Response<LocationUpdateResponse>
}

/**
 * Request model for updating delivery status
 */
data class UpdateDeliveryStatusRequest(
    val status: String, // "PICKED_UP", "DELIVERED", "FAILED"
    val reason: String? = null // Required if status is "FAILED"
)

/**
 * Response model for delivery path/navigation
 */
data class DeliveryPathResponse(
    val restaurantLocation: LocationPoint,
    val customerLocation: LocationPoint,
    val currentLocation: LocationPoint?,
    val distance: Double,
    val estimatedTime: Int
)

data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

/**
 * Request model for updating rider location
 */
data class UpdateLocationRequest(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

/**
 * Response model for location update
 */
data class LocationUpdateResponse(
    val message: String
)
