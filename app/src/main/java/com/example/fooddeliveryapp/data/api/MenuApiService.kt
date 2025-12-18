package com.example.fooddeliveryapp.data.api

import com.example.fooddeliveryapp.data.model.MenuItemsApiResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service for menu operations
 */
interface MenuApiService {

    /**
     * Get menu items for a specific restaurant
     */
    @GET("restaurants/{restaurantId}/menu")
    suspend fun getRestaurantMenu(
        @Path("restaurantId") restaurantId: String,
        @Header("Authorization") authorization: String
    ): Response<MenuItemsApiResponse>
}
