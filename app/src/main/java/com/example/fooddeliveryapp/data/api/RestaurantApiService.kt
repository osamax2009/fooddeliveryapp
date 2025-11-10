package com.example.fooddeliveryapp.data.api

import com.example.fooddeliveryapp.data.model.RestaurantsApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RestaurantApiService {

    @GET("restaurants")
    suspend fun getRestaurants(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Header("Authorization") authorization: String
    ): Response<RestaurantsApiResponse>
}
