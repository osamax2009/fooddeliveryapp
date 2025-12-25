package com.example.fooddeliveryapp.data.repository

import android.util.Log
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.data.api.CartApiService
import com.example.fooddeliveryapp.data.model.AddToCartRequest
import com.example.fooddeliveryapp.data.model.CartResponse
import com.example.fooddeliveryapp.data.model.UpdateCartItemRequest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for cart operations
 */
@Singleton
class CartRepository @Inject constructor(
    private val cartApiService: CartApiService,
    private val sessionManager: SessionManager
) {
    companion object {
        private const val TAG = "CartRepository"
    }

    /**
     * Get current user's cart
     */
    suspend fun getCart(): Result<CartResponse?> {
        return try {
            Log.d(TAG, "getCart: Fetching cart")
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                Log.e(TAG, "getCart: No authentication token found")
                return Result.failure(Exception("No authentication token found"))
            }

            val response = cartApiService.getCart(authorization = "Bearer $token")
            Log.d(TAG, "getCart: Response code: ${response.code()}")

            if (response.isSuccessful) {
                val cartResponse = response.body()
                Log.d(TAG, "getCart: Response body is null: ${cartResponse == null}")

                if (cartResponse == null) {
                    Log.e(TAG, "getCart: Response body is null")
                    Result.failure(Exception("Empty response from server"))
                } else {
                    // Cart API returns CartResponse directly (not wrapped in "data")
                    Log.d(TAG, "getCart: Success - ${cartResponse.items?.size ?: 0} items")
                    Result.success(cartResponse)
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "getCart: Failed - ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to fetch cart: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getCart: Exception - ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Add item to cart
     */
    suspend fun addToCart(restaurantId: String, menuItemId: String, quantity: Int): Result<CartResponse?> {
        return try {
            Log.d(TAG, "addToCart: restaurantId=$restaurantId, menuItemId=$menuItemId, quantity=$quantity")
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                Log.e(TAG, "addToCart: No authentication token found")
                return Result.failure(Exception("No authentication token found"))
            }

            val request = AddToCartRequest(
                restaurantId = restaurantId,
                menuItemId = menuItemId,
                quantity = quantity
            )

            val response = cartApiService.addToCart(
                request = request,
                authorization = "Bearer $token"
            )
            Log.d(TAG, "addToCart: Response code: ${response.code()}")

            if (response.isSuccessful) {
                Log.d(TAG, "addToCart: Item added successfully, fetching updated cart")
                // The API returns {"id": "...", "message": "..."},  not the cart
                // So we need to fetch the cart to get the updated state
                return getCart()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "addToCart: Failed - ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to add to cart: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "addToCart: Exception - ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Update cart item quantity
     */
    suspend fun updateCartItem(cartItemId: String, quantity: Int): Result<CartResponse?> {
        return try {
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No authentication token found"))
            }

            val request = UpdateCartItemRequest(
                cartItemId = cartItemId,
                quantity = quantity
            )

            val response = cartApiService.updateCartItem(
                request = request,
                authorization = "Bearer $token"
            )

            if (response.isSuccessful) {
                val cartResponse = response.body()
                if (cartResponse != null) {
                    // Cart API returns CartResponse directly (not wrapped in "data")
                    Result.success(cartResponse)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Failed to update cart: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Remove item from cart
     */
    suspend fun removeFromCart(cartItemId: String): Result<CartResponse?> {
        return try {
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No authentication token found"))
            }

            val response = cartApiService.removeFromCart(
                cartItemId = cartItemId,
                authorization = "Bearer $token"
            )

            if (response.isSuccessful) {
                val cartResponse = response.body()
                if (cartResponse != null) {
                    // Cart API returns CartResponse directly (not wrapped in "data")
                    Result.success(cartResponse)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Failed to remove from cart: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Clear all items from cart
     */
    suspend fun clearCart(): Result<Unit> {
        return try {
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No authentication token found"))
            }

            val response = cartApiService.clearCart(authorization = "Bearer $token")

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Failed to clear cart: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
