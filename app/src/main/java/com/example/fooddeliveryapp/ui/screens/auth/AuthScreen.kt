package com.example.fooddeliveryapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.ui.features.auth.login.LoginScreen
import javax.inject.Inject

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    sessionManager: SessionManager
) {
    LoginScreen(onLoginSuccess = onLoginSuccess)
}