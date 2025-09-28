package com.example.fooddeliveryapp.ui.features.home.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fooddeliveryapp.data.model.Order
import com.example.fooddeliveryapp.data.model.OrderStatus
import com.example.fooddeliveryapp.ui.screens.HomeScreen.HomeUIEvent
import com.example.fooddeliveryapp.ui.screens.HomeScreen.HomeViewModel

@Composable
fun RestaurantHomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        item {
            RestaurantHeader(
                isOpen = uiState.isRestaurantOpen,
                todayEarnings = uiState.todayEarnings,
                todayOrders = uiState.todayOrdersCount,
                onStatusToggle = { viewModel.onEvent(HomeUIEvent.RestaurantStatusToggled(it)) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Pending Orders",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.isLoading) {
            items(3) {
                OrderCardShimmer()
            }
        } else if (uiState.pendingOrders.isEmpty()) {
            item {
                EmptyOrdersCard()
            }
        } else {
            items(uiState.pendingOrders) { order ->
                OrderCard(
                    order = order,
                    onAccept = { viewModel.onEvent(HomeUIEvent.OrderAccepted(order.id)) },
                    onReject = { viewModel.onEvent(HomeUIEvent.OrderRejected(order.id)) },
                    onStatusChange = { status ->
                        viewModel.onEvent(HomeUIEvent.OrderStatusChanged(order.id, status))
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        uiState.errorMessage?.let { error ->
            item {
                ErrorCard(
                    message = error,
                    onRetry = { viewModel.onEvent(HomeUIEvent.Refresh) },
                    onDismiss = { viewModel.onEvent(HomeUIEvent.ClearError) }
                )
            }
        }
    }
}

@Composable
private fun RestaurantHeader(
    isOpen: Boolean,
    todayEarnings: Double,
    todayOrders: Int,
    onStatusToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOpen) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Restaurant Status",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Switch(
                    checked = isOpen,
                    onCheckedChange = onStatusToggle
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isOpen) "Open for orders" else "Closed",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isOpen) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    icon = Icons.Default.AttachMoney,
                    label = "Today's Earnings",
                    value = "$${String.format("%.2f", todayEarnings)}",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                StatCard(
                    icon = Icons.Default.Restaurant,
                    label = "Orders",
                    value = todayOrders.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onStatusChange: (OrderStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = order.createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Order items
            order.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.quantity}x ${item.menuItem.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$${String.format("%.2f", item.menuItem.price * item.quantity)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider()

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: $${String.format("%.2f", order.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                order.estimatedDelivery?.let { delivery ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Delivery time",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = delivery,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons based on status
            when (order.status) {
                OrderStatus.PENDING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReject,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Reject",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reject")
                        }

                        Button(
                            onClick = onAccept,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Accept",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Accept")
                        }
                    }
                }
                OrderStatus.CONFIRMED -> {
                    Button(
                        onClick = { onStatusChange(OrderStatus.PREPARING) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Preparing")
                    }
                }
                OrderStatus.PREPARING -> {
                    Button(
                        onClick = { onStatusChange(OrderStatus.READY_FOR_PICKUP) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mark Ready for Pickup")
                    }
                }
                else -> {
                    // No actions for other statuses
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: OrderStatus) {
    val (color, text) = when (status) {
        OrderStatus.PENDING -> Color(0xFFFF9800) to "Pending"
        OrderStatus.CONFIRMED -> Color(0xFF2196F3) to "Confirmed"
        OrderStatus.PREPARING -> Color(0xFFFF5722) to "Preparing"
        OrderStatus.READY_FOR_PICKUP -> Color(0xFF4CAF50) to "Ready"
        OrderStatus.OUT_FOR_DELIVERY -> Color(0xFF9C27B0) to "Out for Delivery"
        OrderStatus.DELIVERED -> Color(0xFF4CAF50) to "Delivered"
        OrderStatus.CANCELLED -> Color(0xFFF44336) to "Cancelled"
    }

    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyOrdersCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = "No orders",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No pending orders",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "New orders will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OrderCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(20.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(4.dp)
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(4.dp)
                    )
            )
            Spacer(modifier = Modifier.height(16.dp))
            repeat(2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(4.dp)
                        )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                TextButton(onClick = onRetry) {
                    Text("Retry")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("Dismiss")
                }
            }
        }
    }
}