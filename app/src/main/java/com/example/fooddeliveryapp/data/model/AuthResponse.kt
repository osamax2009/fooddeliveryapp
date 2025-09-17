package com.example.fooddeliveryapp.data.model


data class LoginRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,           // DummyJSON uses 'token' not 'accessToken'
    val refreshToken: String,
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val image: String
)

// ADD: User data structure
data class UserData(
    val id: String,
    val name: String,
    val email: String,
    val userType: String = "CUSTOMER",
    val restaurantId: String? = null,
    val profileImageUrl: String? = null,
    val phoneNumber: String? = null,
    val isEmailVerified: Boolean = false
)