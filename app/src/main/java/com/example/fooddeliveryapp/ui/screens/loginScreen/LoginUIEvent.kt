package com.example.fooddeliveryapp.ui.screens.loginScreen

sealed class LoginUIEvent {
    data class UsernameChanged(val username: String) : LoginUIEvent()
    data class PasswordChanged(val password: String) : LoginUIEvent()
    data class UserTypeChanged(val userType: UserType) : LoginUIEvent()
    object LoginClicked : LoginUIEvent()
    object ClearError : LoginUIEvent()
    object DismissError : LoginUIEvent()
}