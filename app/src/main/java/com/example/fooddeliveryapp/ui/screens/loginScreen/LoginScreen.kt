package com.example.fooddeliveryapp.ui.features.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fooddeliveryapp.ui.screens.loginScreen.LoginUIEvent
import com.example.fooddeliveryapp.ui.screens.loginScreen.UserType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Handle login success
    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Title Section
        Text(
            text = "Welcome to FoodHub",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // User Type Selection
        UserTypeSelector(
            selectedType = uiState.selectedUserType,
            onTypeSelected = { viewModel.onEvent(LoginUIEvent.UserTypeChanged(it)) },
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Username Field
        OutlinedTextField(
            value = uiState.username,
            onValueChange = { viewModel.onEvent(LoginUIEvent.UsernameChanged(it)) },
            label = { Text("Username") },
            placeholder = { Text("Enter your username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = uiState.errorMessage != null && uiState.username.isBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onEvent(LoginUIEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (uiState.username.isNotBlank() && uiState.password.isNotBlank()) {
                        viewModel.onEvent(LoginUIEvent.LoginClicked)
                    }
                }
            ),
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            isError = uiState.errorMessage != null && uiState.password.isBlank()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = { viewModel.onEvent(LoginUIEvent.LoginClicked) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !uiState.isLoading && uiState.username.isNotBlank() && uiState.password.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Signing In...")
            } else {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Demo Credentials
        DemoCredentialsCard()

        // Error Message
        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(
                message = error,
                onDismiss = { viewModel.onEvent(LoginUIEvent.DismissError) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Mode Indicator
        Text(
            text = "App Mode: ${uiState.selectedUserType.name.lowercase().replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun UserTypeSelector(
    selectedType: UserType,
    onTypeSelected: (UserType) -> Unit,
    enabled: Boolean
) {
    Column {
        Text(
            text = "Select User Type",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UserType.entries.forEach { userType ->
                FilterChip(
                    onClick = { onTypeSelected(userType) },
                    label = {
                        Text(
                            text = userType.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    selected = selectedType == userType,
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                )
            }
        }
    }
}

@Composable
private fun DemoCredentialsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Demo Credentials",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Username: emilys\nPassword: emilyspass",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Dismiss")
            }
        }
    }
}