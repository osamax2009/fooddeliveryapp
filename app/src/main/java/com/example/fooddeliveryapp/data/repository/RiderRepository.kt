package com.example.fooddeliveryapp.data.repository

import android.util.Log
import com.example.fooddeliveryapp.data.api.RiderApiService
import com.example.fooddeliveryapp.data.api.UpdateDeliveryStatusRequest
import com.example.fooddeliveryapp.data.api.UpdateLocationRequest
import com.example.fooddeliveryapp.data.model.Order
import com.example.fooddeliveryapp.data.model.toDomainModel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for rider-related operations
 */
@Singleton
class RiderRepository @Inject constructor(
    private val riderApiService: RiderApiService
) {

    companion object {
        private const val TAG = "RiderRepository"
    }

    /**
     * Get all available delivery requests
     * @return Result with list of available orders
     */
    suspend fun getAvailableDeliveries(): Result<List<Order>> {
        return try {
            Log.d(TAG, "Fetching available deliveries")
            val response = riderApiService.getAvailableDeliveries()

            if (response.isSuccessful) {
                val ordersResponse = response.body()
                if (ordersResponse?.data != null) {
                    val orders = ordersResponse.data.map { it.toDomainModel() }
                    Log.d(TAG, "Successfully fetched ${orders.size} available deliveries")
                    Result.success(orders)
                } else {
                    Log.e(TAG, "Empty response from server")
                    Result.failure(Exception("No deliveries available"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Failed to fetch deliveries: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to fetch deliveries: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while fetching deliveries: ${e.message}", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Get rider's active deliveries
     * @return Result with list of active orders
     */
    suspend fun getActiveDeliveries(): Result<List<Order>> {
        return try {
            Log.d(TAG, "Fetching active deliveries")
            val response = riderApiService.getActiveDeliveries()

            if (response.isSuccessful) {
                val ordersResponse = response.body()
                if (ordersResponse?.data != null) {
                    val orders = ordersResponse.data.map { it.toDomainModel() }
                    Log.d(TAG, "Successfully fetched ${orders.size} active deliveries")
                    Result.success(orders)
                } else {
                    Result.success(emptyList()) // No active deliveries
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Failed to fetch active deliveries: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to fetch active deliveries"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while fetching active deliveries: ${e.message}", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Accept a delivery request
     * @param orderId The order ID to accept
     * @return Result with the accepted order
     */
    suspend fun acceptDelivery(orderId: String): Result<Order> {
        return try {
            Log.d(TAG, "Accepting delivery: $orderId")
            val response = riderApiService.acceptDelivery(orderId)

            if (response.isSuccessful) {
                val orderResponse = response.body()
                if (orderResponse?.data != null) {
                    val order = orderResponse.data.toDomainModel()
                    Log.d(TAG, "Successfully accepted delivery: $orderId")
                    Result.success(order)
                } else {
                    Log.e(TAG, "Empty response from server")
                    Result.failure(Exception("Failed to accept delivery"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Failed to accept delivery: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to accept delivery: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while accepting delivery: ${e.message}", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Reject a delivery request
     * @param orderId The order ID to reject
     * @return Result with success or failure
     */
    suspend fun rejectDelivery(orderId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Rejecting delivery: $orderId")
            val response = riderApiService.rejectDelivery(orderId)

            if (response.isSuccessful) {
                Log.d(TAG, "Successfully rejected delivery: $orderId")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Failed to reject delivery: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to reject delivery"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while rejecting delivery: ${e.message}", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Update delivery status
     * @param orderId The order ID
     * @param status The new status (PICKED_UP, DELIVERED, FAILED)
     * @param reason Optional reason (required if status is FAILED)
     * @return Result with the updated order
     */
    suspend fun updateDeliveryStatus(
        orderId: String,
        status: String,
        reason: String? = null
    ): Result<Order> {
        return try {
            Log.d(TAG, "Updating delivery status: $orderId to $status")
            val request = UpdateDeliveryStatusRequest(status = status, reason = reason)
            val response = riderApiService.updateDeliveryStatus(orderId, request)

            if (response.isSuccessful) {
                val orderResponse = response.body()
                if (orderResponse?.data != null) {
                    val order = orderResponse.data.toDomainModel()
                    Log.d(TAG, "Successfully updated delivery status: $orderId")
                    Result.success(order)
                } else {
                    Log.e(TAG, "Empty response from server")
                    Result.failure(Exception("Failed to update delivery status"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Failed to update delivery status: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to update delivery status"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while updating delivery status: ${e.message}", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Update rider's current location
     * @param latitude Current latitude
     * @param longitude Current longitude
     * @param address Current address
     * @return Result with success or failure
     */
    suspend fun updateLocation(
        latitude: Double,
        longitude: Double,
        address: String
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Updating rider location: ($latitude, $longitude) - $address")
            val request = UpdateLocationRequest(
                latitude = latitude,
                longitude = longitude,
                address = address
            )
            val response = riderApiService.updateLocation(request)

            if (response.isSuccessful) {
                val locationResponse = response.body()
                if (locationResponse != null) {
                    Log.d(TAG, "Successfully updated location: ${locationResponse.message}")
                    Result.success(Unit)
                } else {
                    Log.e(TAG, "Empty response from server")
                    Result.failure(Exception("Failed to update location"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Failed to update location: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to update location: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while updating location: ${e.message}", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}
