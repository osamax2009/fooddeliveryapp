package com.example.fooddeliveryapp.data.api

import com.example.fooddeliveryapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service for order-related operations
 */
interface OrderApiService {

    /**
     * Place a new order from the current cart
     */
    @POST("orders")
    suspend fun placeOrder(
        @Body request: PlaceOrderRequest,
        @Header("Authorization") authorization: String
    ): Response<OrderApiResponse>

    /**
     * Get all orders for the authenticated user
     */
    @GET("orders")
    suspend fun getUserOrders(
        @Header("Authorization") authorization: String
    ): Response<OrdersApiResponse>

    /**
     * Get details for a specific order
     */
    @GET("orders/{orderId}")
    suspend fun getOrderDetails(
        @Path("orderId") orderId: String,
        @Header("Authorization") authorization: String
    ): Response<OrderApiResponse>

    /**
     * Update order status (for restaurant owners and riders)
     */
    @PATCH("orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body request: UpdateOrderStatusRequest,
        @Header("Authorization") authorization: String
    ): Response<OrderApiResponse>
}
