package com.example.fooddeliveryapp.data.api

import com.example.fooddeliveryapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service for address-related operations
 */
interface AddressApiService {

    /**
     * Get all addresses for the authenticated user
     */
    @GET("addresses")
    suspend fun getUserAddresses(
        @Header("Authorization") authorization: String
    ): Response<AddressesApiResponse>

    /**
     * Add a new address
     */
    @POST("addresses")
    suspend fun addAddress(
        @Body request: AddAddressRequest,
        @Header("Authorization") authorization: String
    ): Response<AddressApiResponse>

    /**
     * Update an existing address
     */
    @PUT("addresses/{addressId}")
    suspend fun updateAddress(
        @Path("addressId") addressId: String,
        @Body request: AddAddressRequest,
        @Header("Authorization") authorization: String
    ): Response<AddressApiResponse>

    /**
     * Delete an address
     */
    @DELETE("addresses/{addressId}")
    suspend fun deleteAddress(
        @Path("addressId") addressId: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    /**
     * Reverse geocode coordinates to address
     */
    @POST("addresses/reverse-geocode")
    suspend fun reverseGeocode(
        @Body request: ReverseGeocodeRequest,
        @Header("Authorization") authorization: String
    ): Response<ReverseGeocodeResponse>
}