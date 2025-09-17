package com.example.fooddeliveryapp.ui.screens.loginScreen

data class LoginUIState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val selectedUserType: UserType = UserType.CUSTOMER
)

enum class UserType {
    CUSTOMER,
    RESTAURANT,
    RIDER
}