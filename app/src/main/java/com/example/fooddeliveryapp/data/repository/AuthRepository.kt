package com.example.fooddeliveryapp.data.repository

import com.example.fooddeliveryapp.data.api.AuthApiService
import com.example.fooddeliveryapp.data.model.AuthResponse
import com.example.fooddeliveryapp.data.model.LoginRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Validate that we have a token
                if (authResponse.accessToken.isBlank()) {
                    Result.failure(Exception("Login failed: No token received"))
                } else {
                    Result.success(authResponse)
                }
            } else {
                val errorCode = response.code()
                val errorMessage = when (errorCode) {
                    400 -> "Invalid username or password"
                    401 -> "Authentication failed"
                    404 -> "Service not found"
                    500 -> "Server error"
                    else -> "Login failed: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}