package com.example.fooddeliveryapp.data.api

import com.example.fooddeliveryapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service for cart operations
 */
interface CartApiService {

    /**
     * Get current user's cart
     */
    @GET("cart")
    suspend fun getCart(
        @Header("Authorization") authorization: String
    ): Response<CartApiResponse>

    /**
     * Add item to cart
     */
    @POST("cart")
    suspend fun addToCart(
        @Body request: AddToCartRequest,
        @Header("Authorization") authorization: String
    ): Response<CartApiResponse>

    /**
     * Update cart item quantity
     */
    @PATCH("cart")
    suspend fun updateCartItem(
        @Body request: UpdateCartItemRequest,
        @Header("Authorization") authorization: String
    ): Response<CartApiResponse>

    /**
     * Remove item from cart
     */
    @DELETE("cart/{cartItemId}")
    suspend fun removeFromCart(
        @Path("cartItemId") cartItemId: String,
        @Header("Authorization") authorization: String
    ): Response<CartApiResponse>

    /**
     * Clear all items from cart
     */
    @DELETE("cart")
    suspend fun clearCart(
        @Header("Authorization") authorization: String
    ): Response<Unit>
}
