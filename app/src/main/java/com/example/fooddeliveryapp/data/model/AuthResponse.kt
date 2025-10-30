package com.example.fooddeliveryapp.data.model

import com.google.gson.annotations.SerializedName

// Login Request
data class LoginRequest(
    val email: String,
    val password: String
)

// Signup Request
data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String // "customer", "owner", or "rider"
)

// OAuth Request
data class OAuthRequest(
    val provider: String, // "google" or "facebook"
    val token: String,
    val type: String // "customer", "owner", or "rider"
)

// Auth Response (for both login and signup)
data class AuthResponse(
    val token: String,
    @SerializedName("user")
    val user: UserData? = null,
    val message: String? = null
)

// User Data Structure
data class UserData(
    val id: String,
    val name: String,
    val email: String,
    val role: String, // "customer", "owner", or "rider"
    val restaurantId: String? = null,
    val profileImageUrl: String? = null,
    val phoneNumber: String? = null,
    val isEmailVerified: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)