package com.example.fooddeliveryapp.ui.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.data.repository.AuthRepository
import com.example.fooddeliveryapp.ui.screens.loginScreen.LoginUIEvent
import com.example.fooddeliveryapp.ui.screens.loginScreen.LoginUIState
import com.example.fooddeliveryapp.ui.screens.loginScreen.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    init {
        // Set default user type based on build variant
        val defaultUserType = when (sessionManager.getUserType()) {
            SessionManager.UserType.CUSTOMER -> UserType.CUSTOMER
            SessionManager.UserType.RESTAURANT -> UserType.RESTAURANT
            SessionManager.UserType.RIDER -> UserType.RIDER
        }
        _uiState.value = _uiState.value.copy(selectedUserType = defaultUserType)
    }

    fun onEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.UsernameChanged -> {
                _uiState.value = _uiState.value.copy(
                    username = event.username,
                    errorMessage = null
                )
            }
            is LoginUIEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    password = event.password,
                    errorMessage = null
                )
            }
            is LoginUIEvent.UserTypeChanged -> {
                _uiState.value = _uiState.value.copy(
                    selectedUserType = event.userType,
                    errorMessage = null
                )
            }
            is LoginUIEvent.LoginClicked -> {
                login()
            }
            is LoginUIEvent.ClearError -> {
                _uiState.value = _uiState.value.copy(errorMessage = null)
            }
            is LoginUIEvent.DismissError -> {
                _uiState.value = _uiState.value.copy(errorMessage = null)
            }
        }
    }

    private fun login() {
        if (!isFormValid()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please fill in all fields"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.login(_uiState.value.username, _uiState.value.password)

            result.fold(
                onSuccess = { authResponse ->
                    // Store token and user type in session
                    sessionManager.storeToken(authResponse.accessToken)
                    //sessionManager.st(_uiState.value.selectedUserType.name)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
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
        return _uiState.value.username.isNotBlank() &&
                _uiState.value.password.isNotBlank()
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("401") == true -> "Invalid username or password"
            exception.message?.contains("network") == true -> "Network error. Please check your connection"
            else -> exception.message ?: "Login failed. Please try again"
        }
    }
}