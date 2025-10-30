package com.example.fooddeliveryapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.ui.features.auth.login.LoginScreen
import com.example.fooddeliveryapp.ui.features.auth.signup.SignUpView
import javax.inject.Inject

enum class AuthScreenState {
    LOGIN,
    SIGNUP
}

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    sessionManager: SessionManager
) {
    var screenState by remember { mutableStateOf(AuthScreenState.LOGIN) }

    when (screenState) {
        AuthScreenState.LOGIN -> {
            LoginScreen(
                onLoginSuccess = onLoginSuccess,
                onNavigateToSignup = { screenState = AuthScreenState.SIGNUP }
            )
        }
        AuthScreenState.SIGNUP -> {
            SignUpView(
                onSignupSuccess = onLoginSuccess,
                onNavigateToLogin = { screenState = AuthScreenState.LOGIN }
            )
        }
    }
}