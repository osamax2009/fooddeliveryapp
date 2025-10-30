package com.example.fooddeliveryapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.data.model.UserData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    sessionManager: SessionManager,
    onLogout: () -> Unit
) {
    val userData = remember { sessionManager.getUserData() }
    val userType by sessionManager.userType.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(
                    onClick = {
                        sessionManager.clearSession()
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Profile") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Text(
                text = userData?.name ?: "User",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // User Email
            Text(
                text = userData?.email ?: "email@example.com",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // User Type Chip
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = when (userType) {
                            SessionManager.UserType.CUSTOMER -> "Customer"
                            SessionManager.UserType.RESTAURANT -> "Restaurant Owner"
                            SessionManager.UserType.RIDER -> "Delivery Rider"
                        }
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = when (userType) {
                            SessionManager.UserType.CUSTOMER -> Icons.Default.ShoppingCart
                            SessionManager.UserType.RESTAURANT -> Icons.Default.Store
                            SessionManager.UserType.RIDER -> Icons.Default.DirectionsBike
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Account Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // User ID
                    userData?.id?.let { id ->
                        ProfileInfoRow(
                            icon = Icons.Default.Badge,
                            label = "User ID",
                            value = id.take(8) + "..."
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Phone Number
                    userData?.phoneNumber?.let { phone ->
                        ProfileInfoRow(
                            icon = Icons.Default.Phone,
                            label = "Phone",
                            value = phone
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Restaurant ID (for restaurant owners)
                    if (userType == SessionManager.UserType.RESTAURANT) {
                        userData?.restaurantId?.let { restaurantId ->
                            ProfileInfoRow(
                                icon = Icons.Default.Store,
                                label = "Restaurant ID",
                                value = restaurantId.take(8) + "..."
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Email Verification Status
                    ProfileInfoRow(
                        icon = if (userData?.isEmailVerified == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        label = "Email Status",
                        value = if (userData?.isEmailVerified == true) "Verified" else "Not Verified",
                        valueColor = if (userData?.isEmailVerified == true)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Actions
            ProfileActionButton(
                icon = Icons.Default.Edit,
                text = "Edit Profile",
                onClick = { /* TODO: Navigate to edit profile */ }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileActionButton(
                icon = Icons.Default.Lock,
                text = "Change Password",
                onClick = { /* TODO: Navigate to change password */ }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileActionButton(
                icon = Icons.Default.Settings,
                text = "Settings",
                onClick = { /* TODO: Navigate to settings */ }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileActionButton(
                icon = Icons.Default.Help,
                text = "Help & Support",
                onClick = { /* TODO: Navigate to help */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Version
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = valueColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ProfileActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
