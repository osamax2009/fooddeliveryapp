package com.example.fooddeliveryapp.data.repository

import com.example.fooddeliveryapp.data.api.AuthApiService
import com.example.fooddeliveryapp.data.model.AuthResponse
import com.example.fooddeliveryapp.data.model.LoginRequest
import com.example.fooddeliveryapp.data.model.OAuthRequest
import com.example.fooddeliveryapp.data.model.SignupRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    /**
     * Login user with email and password
     */
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Validate that we have a token
                if (authResponse.token.isBlank()) {
                    Result.failure(Exception("Login failed: No token received"))
                } else {
                    Result.success(authResponse)
                }
            } else {
                val errorCode = response.code()
                val errorMessage = when (errorCode) {
                    400 -> "Invalid email or password"
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

    /**
     * Register new user
     * @param name User's full name
     * @param email User's email
     * @param password User's password
     * @param role User role: "customer", "owner", or "rider"
     */
    suspend fun signup(
        name: String,
        email: String,
        password: String,
        role: String
    ): Result<AuthResponse> {
        return try {
            val response = authApiService.signup(
                SignupRequest(
                    name = name,
                    email = email,
                    password = password,
                    role = role
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                if (authResponse.token.isBlank()) {
                    Result.failure(Exception("Signup failed: No token received"))
                } else {
                    Result.success(authResponse)
                }
            } else {
                val errorCode = response.code()
                val errorMessage = when (errorCode) {
                    400 -> "Invalid registration data"
                    409 -> "User already exists"
                    500 -> "Server error"
                    else -> "Signup failed: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Login with OAuth (Google or Facebook)
     * @param provider "google" or "facebook"
     * @param token OAuth access token from provider
     * @param type User type: "customer", "owner", or "rider"
     */
    suspend fun oauthLogin(
        provider: String,
        token: String,
        type: String
    ): Result<AuthResponse> {
        return try {
            val response = authApiService.oauthLogin(
                OAuthRequest(
                    provider = provider,
                    token = token,
                    type = type
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                if (authResponse.token.isBlank()) {
                    Result.failure(Exception("OAuth login failed: No token received"))
                } else {
                    Result.success(authResponse)
                }
            } else {
                val errorCode = response.code()
                val errorMessage = when (errorCode) {
                    400 -> "Invalid OAuth token"
                    401 -> "OAuth authentication failed"
                    500 -> "Server error"
                    else -> "OAuth login failed: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}