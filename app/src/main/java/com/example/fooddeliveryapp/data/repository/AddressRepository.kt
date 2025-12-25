package com.example.fooddeliveryapp.data.repository

import android.util.Log
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.data.api.AddressApiService
import com.example.fooddeliveryapp.data.model.AddAddressRequest
import com.example.fooddeliveryapp.data.model.AddressResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for address-related operations
 */
@Singleton
class AddressRepository @Inject constructor(
    private val addressApiService: AddressApiService,
    private val sessionManager: SessionManager
) {
    companion object {
        private const val TAG = "AddressRepository"
    }

    /**
     * Get all addresses for the authenticated user
     */
    suspend fun getUserAddresses(): Result<List<AddressResponse>> {
        return try {
            Log.d(TAG, "getUserAddresses: Fetching user addresses")
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                Log.e(TAG, "getUserAddresses: No authentication token found")
                return Result.failure(Exception("No authentication token found"))
            }

            val response = addressApiService.getUserAddresses(
                authorization = "Bearer $token"
            )
            Log.d(TAG, "getUserAddresses: Response code: ${response.code()}")

            if (response.isSuccessful) {
                val addressesResponse = response.body()
                Log.d(TAG, "getUserAddresses: Response body is null: ${addressesResponse == null}")

                if (addressesResponse?.data != null) {
                    Log.d(TAG, "getUserAddresses: Success - ${addressesResponse.data.size} addresses found")
                    Result.success(addressesResponse.data)
                } else {
                    // Return empty list if no addresses
                    Log.d(TAG, "getUserAddresses: No addresses found, returning empty list")
                    Result.success(emptyList())
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "getUserAddresses: Failed - ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to fetch addresses: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getUserAddresses: Exception - ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Add a new address
     */
    suspend fun addAddress(
        addressLine1: String,
        addressLine2: String?,
        city: String,
        state: String,
        zipCode: String,
        country: String,
        latitude: Double,
        longitude: Double
    ): Result<AddressResponse> {
        return try {
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No authentication token found"))
            }

            val request = AddAddressRequest(
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
                city = city,
                state = state,
                zipCode = zipCode,
                country = country,
                latitude = latitude,
                longitude = longitude
            )

            val response = addressApiService.addAddress(
                request = request,
                authorization = "Bearer $token"
            )

            if (response.isSuccessful) {
                val addressResponse = response.body()
                if (addressResponse?.data != null) {
                    Result.success(addressResponse.data)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Failed to add address: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete an address
     */
    suspend fun deleteAddress(addressId: String): Result<Unit> {
        return try {
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No authentication token found"))
            }

            val response = addressApiService.deleteAddress(
                addressId = addressId,
                authorization = "Bearer $token"
            )

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Failed to delete address: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}