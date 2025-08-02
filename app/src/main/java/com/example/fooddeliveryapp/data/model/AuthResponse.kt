package com.example.fooddeliveryapp.data.model


data class AuthResponse(
    val token: String,
    val user: UserData? = null
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