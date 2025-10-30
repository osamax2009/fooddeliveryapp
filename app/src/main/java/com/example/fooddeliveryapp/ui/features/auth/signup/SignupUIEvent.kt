package com.example.fooddeliveryapp.ui.features.auth.signup

sealed class SignupUIEvent {
    data class NameChanged(val name: String) : SignupUIEvent()
    data class EmailChanged(val email: String) : SignupUIEvent()
    data class PasswordChanged(val password: String) : SignupUIEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : SignupUIEvent()
    data class UserTypeChanged(val userType: UserType) : SignupUIEvent()
    object SignupClicked : SignupUIEvent()
    object DismissError : SignupUIEvent()
}
