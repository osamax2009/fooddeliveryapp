package com.example.fooddeliveryapp.ui.features.auth.signup

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpView(
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Handle signup success
    LaunchedEffect(uiState.isSignupSuccessful) {
        if (uiState.isSignupSuccessful) {
            onSignupSuccess()
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
            text = "Create Account",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign up to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // User Type Selection
        UserTypeSelector(
            selectedType = uiState.selectedUserType,
            onTypeSelected = { viewModel.onEvent(SignupUIEvent.UserTypeChanged(it)) },
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Name Field
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.onEvent(SignupUIEvent.NameChanged(it)) },
            label = { Text("Full Name") },
            placeholder = { Text("Enter your full name") },
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
            isError = uiState.errorMessage != null && uiState.name.isBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEvent(SignupUIEvent.EmailChanged(it)) },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = uiState.errorMessage != null && uiState.email.isBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onEvent(SignupUIEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
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

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Field
        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = { viewModel.onEvent(SignupUIEvent.ConfirmPasswordChanged(it)) },
            label = { Text("Confirm Password") },
            placeholder = { Text("Confirm your password") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (isFormFilled(uiState)) {
                        viewModel.onEvent(SignupUIEvent.SignupClicked)
                    }
                }
            ),
            trailingIcon = {
                IconButton(
                    onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            isError = uiState.errorMessage != null && uiState.confirmPassword.isBlank()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Signup Button
        Button(
            onClick = { viewModel.onEvent(SignupUIEvent.SignupClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isLoading && isFormFilled(uiState)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Creating Account...")
            } else {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to Login
        TextButton(
            onClick = onNavigateToLogin,
            enabled = !uiState.isLoading
        ) {
            Text(
                text = "Already have an account? Sign In",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Error Message
        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(
                message = error,
                onDismiss = { viewModel.onEvent(SignupUIEvent.DismissError) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Mode Indicator
        Text(
            text = "Signing up as ${uiState.selectedUserType.displayName}",
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
            text = "Select Account Type",
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
                            text = userType.displayName,
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

private fun isFormFilled(uiState: SignupUIState): Boolean {
    return uiState.name.isNotBlank() &&
            uiState.email.isNotBlank() &&
            uiState.password.isNotBlank() &&
            uiState.confirmPassword.isNotBlank()
}
