package com.example.fooddeliveryapp.data.repository

import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.data.api.OrderApiService
import com.example.fooddeliveryapp.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for order-related operations
 */
@Singleton
class OrderRepository @Inject constructor(
    private val orderApiService: OrderApiService,
    private val sessionManager: SessionManager
) {

    /**
     * Place a new order from the current cart
     * @param addressId The delivery address ID
     * @return Result with the created order
     */
    suspend fun placeOrder(addressId: String): Result<Order> {
        return try {
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No authentication token found"))
            }

            val request = PlaceOrderRequest(addressId = addressId)
            val response = orderApiService.placeOrder(
                request = request,
                authorization = "Bearer $token"
            )

            if (response.isSuccessful) {
                val orderResponse = response.body()
                if (orderResponse != null) {
                    val order = orderResponse.data.toDomainModel()
                    Result.success(order)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Failed to place order: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all orders for the authenticated user
     * @return Result with list of orders
     */
    suspend fun getUserOrders(): Result<List<Order>> {
        return try {
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No authentication token found"))
            }

            val response = orderApiService.getUserOrders(
                authorization = "Bearer $token"
            )

            if (response.isSuccessful) {
                val ordersResponse = response.body()
                if (ordersResponse != null) {
                    val orders = ordersResponse.data.map { it.toDomainModel() }
                    Result.success(orders)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Failed to fetch orders: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get details for a specific order
     * @param orderId The order ID
     * @return Result with the order details
     */
    suspend fun getOrderDetails(orderId: String): Result<Order> {
        return try {
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No authentication token found"))
            }

            val response = orderApiService.getOrderDetails(
                orderId = orderId,
                authorization = "Bearer $token"
            )

            if (response.isSuccessful) {
                val orderResponse = response.body()
                if (orderResponse != null) {
                    val order = orderResponse.data.toDomainModel()
                    Result.success(order)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Failed to fetch order details: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update order status (for restaurant owners and riders)
     * @param orderId The order ID
     * @param status The new status
     * @return Result with the updated order
     */
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Order> {
        return try {
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No authentication token found"))
            }

            val statusString = when (status) {
                OrderStatus.PENDING -> "PENDING_ACCEPTANCE"
                OrderStatus.CONFIRMED -> "ACCEPTED"
                OrderStatus.PREPARING -> "PREPARING"
                OrderStatus.READY_FOR_PICKUP -> "READY"
                OrderStatus.OUT_FOR_DELIVERY -> "OUT_FOR_DELIVERY"
                OrderStatus.DELIVERED -> "DELIVERED"
                OrderStatus.CANCELLED -> "CANCELLED"
            }

            val request = UpdateOrderStatusRequest(status = statusString)
            val response = orderApiService.updateOrderStatus(
                orderId = orderId,
                request = request,
                authorization = "Bearer $token"
            )

            if (response.isSuccessful) {
                val orderResponse = response.body()
                if (orderResponse != null) {
                    val order = orderResponse.data.toDomainModel()
                    Result.success(order)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Failed to update order status: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
