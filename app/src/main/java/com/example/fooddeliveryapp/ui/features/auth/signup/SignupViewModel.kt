package com.example.fooddeliveryapp.ui.features.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUIState())
    val uiState: StateFlow<SignupUIState> = _uiState.asStateFlow()

    init {
        // Set default user type based on build variant
        val defaultUserType = when (sessionManager.getUserType()) {
            SessionManager.UserType.CUSTOMER -> UserType.CUSTOMER
            SessionManager.UserType.RESTAURANT -> UserType.RESTAURANT
            SessionManager.UserType.RIDER -> UserType.RIDER
        }
        _uiState.value = _uiState.value.copy(selectedUserType = defaultUserType)
    }

    fun onEvent(event: SignupUIEvent) {
        when (event) {
            is SignupUIEvent.NameChanged -> {
                _uiState.value = _uiState.value.copy(
                    name = event.name,
                    errorMessage = null
                )
            }
            is SignupUIEvent.EmailChanged -> {
                _uiState.value = _uiState.value.copy(
                    email = event.email,
                    errorMessage = null
                )
            }
            is SignupUIEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    password = event.password,
                    errorMessage = null
                )
            }
            is SignupUIEvent.ConfirmPasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    confirmPassword = event.confirmPassword,
                    errorMessage = null
                )
            }
            is SignupUIEvent.UserTypeChanged -> {
                _uiState.value = _uiState.value.copy(
                    selectedUserType = event.userType,
                    errorMessage = null
                )
            }
            is SignupUIEvent.SignupClicked -> {
                signup()
            }
            is SignupUIEvent.DismissError -> {
                _uiState.value = _uiState.value.copy(errorMessage = null)
            }
        }
    }

    private fun signup() {
        // Validate form
        if (!isFormValid()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please fill in all fields"
            )
            return
        }

        // Validate email format
        if (!isValidEmail(_uiState.value.email)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter a valid email address"
            )
            return
        }

        // Validate password length
        if (_uiState.value.password.length < 6) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Password must be at least 6 characters"
            )
            return
        }

        // Validate password match
        if (_uiState.value.password != _uiState.value.confirmPassword) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Passwords do not match"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.signup(
                name = _uiState.value.name,
                email = _uiState.value.email,
                password = _uiState.value.password,
                role = _uiState.value.selectedUserType.apiRole
            )

            result.fold(
                onSuccess = { authResponse ->
                    // Store token and user data in session
                    sessionManager.storeToken(authResponse.token)
                    sessionManager.storeUserData(authResponse.user)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSignupSuccessful = true,
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getErrorMessage(exception)
                    )
                }
            )
        }
    }

    private fun isFormValid(): Boolean {
        return _uiState.value.name.isNotBlank() &&
                _uiState.value.email.isNotBlank() &&
                _uiState.value.password.isNotBlank() &&
                _uiState.value.confirmPassword.isNotBlank()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("409") == true -> "User already exists with this email"
            exception.message?.contains("400") == true -> "Invalid registration data"
            exception.message?.contains("network") == true -> "Network error. Please check your connection"
            else -> exception.message ?: "Signup failed. Please try again"
        }
    }
}
