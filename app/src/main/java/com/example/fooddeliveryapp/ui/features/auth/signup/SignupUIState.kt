package com.example.fooddeliveryapp.ui.features.auth.signup

data class SignupUIState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val selectedUserType: UserType = UserType.CUSTOMER,
    val isLoading: Boolean = false,
    val isSignupSuccessful: Boolean = false,
    val errorMessage: String? = null
)

enum class UserType(val displayName: String, val apiRole: String) {
    CUSTOMER("Customer", "customer"),
    RESTAURANT("Restaurant Owner", "owner"),
    RIDER("Delivery Rider", "rider")
}
