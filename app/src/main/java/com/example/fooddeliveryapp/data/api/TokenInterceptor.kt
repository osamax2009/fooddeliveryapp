package com.example.fooddeliveryapp.data.api

import android.util.Log
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.data.model.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * TokenInterceptor handles:
 * 1. Automatically adding Authorization header to all requests
 * 2. Detecting 401 Unauthorized responses
 * 3. Refreshing access token using refresh token
 * 4. Retrying the original request with new token
 */
@Singleton
class TokenInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    private val authApiServiceProvider: Provider<AuthApiService>
) : Interceptor {

    companion object {
        private const val TAG = "TokenInterceptor"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_RETRY_COUNT = "X-Retry-Count"
        private const val MAX_RETRY_COUNT = 3
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip adding auth header for auth endpoints
        val isAuthEndpoint = originalRequest.url.encodedPath.contains("/auth/")

        // Add Authorization header if token exists and not auth endpoint
        val request = if (!isAuthEndpoint) {
            val token = sessionManager.getToken()
            if (!token.isNullOrEmpty()) {
                originalRequest.newBuilder()
                    .header(HEADER_AUTHORIZATION, "Bearer $token")
                    .build()
            } else {
                originalRequest
            }
        } else {
            originalRequest
        }

        // Proceed with the request
        var response = chain.proceed(request)

        // Check if we got a 401 Unauthorized and attempt token refresh
        if (response.code == 401 && !isAuthEndpoint) {
            Log.d(TAG, "Received 401 Unauthorized, attempting token refresh")

            // Check retry count to prevent infinite loops
            val retryCount = request.header(HEADER_RETRY_COUNT)?.toIntOrNull() ?: 0
            if (retryCount >= MAX_RETRY_COUNT) {
                Log.e(TAG, "Max retry count reached, clearing session")
                sessionManager.clearSession()
                return response
            }

            // Close the original response
            response.close()

            // Attempt to refresh the token
            val newToken = runBlocking {
                refreshAccessToken()
            }

            if (newToken != null) {
                Log.d(TAG, "Token refreshed successfully, retrying request")

                // Retry the request with the new token
                val newRequest = originalRequest.newBuilder()
                    .header(HEADER_AUTHORIZATION, "Bearer $newToken")
                    .header(HEADER_RETRY_COUNT, (retryCount + 1).toString())
                    .build()

                response = chain.proceed(newRequest)
            } else {
                Log.e(TAG, "Failed to refresh token, clearing session")
                sessionManager.clearSession()
            }
        }

        return response
    }

    /**
     * Attempts to refresh the access token using the refresh token
     */
    private suspend fun refreshAccessToken(): String? {
        val refreshToken = sessionManager.getRefreshToken()
        if (refreshToken.isNullOrEmpty()) {
            Log.e(TAG, "No refresh token available")
            return null
        }

        return try {
            Log.d(TAG, "Calling refresh token API")
            val authApiService = authApiServiceProvider.get()
            val response = authApiService.refreshToken(
                RefreshTokenRequest(
                    refreshToken = refreshToken,
                    deviceInfo = "Android App"
                )
            )

            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    Log.d(TAG, "Refresh token API successful")

                    // Store the new tokens
                    sessionManager.storeToken(authResponse.token)
                    authResponse.refreshToken?.let {
                        sessionManager.storeRefreshToken(it)
                    }

                    authResponse.token
                } else {
                    Log.e(TAG, "Refresh token response body is null")
                    null
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Refresh token API failed: ${response.code()} - $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in refreshAccessToken: ${e.message}", e)
            null
        }
    }
}
