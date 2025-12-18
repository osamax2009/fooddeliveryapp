package com.example.fooddeliveryapp.ui.screens.HomeScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.navigation.CheckoutRoute
import com.example.fooddeliveryapp.navigation.OrderDetailsRoute
import com.example.fooddeliveryapp.data.model.Restaurant
import com.example.fooddeliveryapp.ui.components.BottomNavigationBar
import com.example.fooddeliveryapp.ui.features.cart.CartScreen
import com.example.fooddeliveryapp.ui.features.home.customer.CustomerHomeScreen
import com.example.fooddeliveryapp.ui.features.home.restaurant.RestaurantHomeScreen
import com.example.fooddeliveryapp.ui.features.orders.OrderListScreen
import com.example.fooddeliveryapp.ui.features.restaurant.RestaurantDetailScreen
import com.example.fooddeliveryapp.ui.screens.profile.ProfileScreen

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    sessionManager: SessionManager,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userType by sessionManager.userType.collectAsStateWithLifecycle()
    var currentRoute by remember { mutableStateOf("home") }
    var selectedOrderId by remember { mutableStateOf<String?>(null) }
    var showCheckout by remember { mutableStateOf(false) }
    var selectedRestaurant by remember { mutableStateOf<Restaurant?>(null) }
    var showCart by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                userType = userType,
                currentRoute = currentRoute,
                onNavigate = { route -> currentRoute = route },
                pendingOrdersCount = when (userType) {
                    SessionManager.UserType.RESTAURANT -> uiState.pendingOrders.size
                    SessionManager.UserType.CUSTOMER -> uiState.recentOrders.count {
                        it.status in listOf(
                            com.example.fooddeliveryapp.data.model.OrderStatus.CONFIRMED,
                            com.example.fooddeliveryapp.data.model.OrderStatus.PREPARING,
                            com.example.fooddeliveryapp.data.model.OrderStatus.OUT_FOR_DELIVERY
                        )
                    }
                    else -> 0
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentRoute) {
                "home", "dashboard", "rider_home" -> {
                    when {
                        showCart -> {
                            // Show cart screen
                            CartScreen(
                                onBackClick = { showCart = false },
                                onCheckoutClick = {
                                    showCart = false
                                    showCheckout = true
                                }
                            )
                        }
                        showCheckout -> {
                            // Show checkout screen
                            com.example.fooddeliveryapp.ui.features.orders.CheckoutScreen(
                                onBackClick = { showCheckout = false },
                                onOrderPlaced = { orderId ->
                                    showCheckout = false
                                    selectedOrderId = orderId
                                    currentRoute = "orders"
                                }
                            )
                        }
                        selectedRestaurant != null -> {
                            // Show restaurant detail screen
                            RestaurantDetailScreen(
                                restaurant = selectedRestaurant!!,
                                onBackClick = { selectedRestaurant = null },
                                onCartClick = { showCart = true }
                            )
                        }
                        else -> {
                            when (userType) {
                                SessionManager.UserType.CUSTOMER -> {
                                    CustomerHomeScreen(
                                        onRestaurantClick = { restaurant ->
                                            selectedRestaurant = restaurant
                                        },
                                        onLocationClick = {
                                            // TODO: Open location picker
                                        },
                                        onCheckoutClick = {
                                            showCart = true
                                        }
                                    )
                                }
                                SessionManager.UserType.RESTAURANT -> {
                                    RestaurantHomeScreen()
                                }
                                SessionManager.UserType.RIDER -> {
                                    RiderHomeScreen()
                                }
                            }
                        }
                    }
                }
                "search" -> {
                    // TODO: Implement Search Screen
                    PlaceholderScreen("Search Screen")
                }
                "orders" -> {
                    if (selectedOrderId != null) {
                        // Show order details
                        com.example.fooddeliveryapp.ui.features.orders.OrderDetailsScreen(
                            orderId = selectedOrderId!!,
                            onBackClick = { selectedOrderId = null }
                        )
                    } else {
                        // Show order list
                        OrderListScreen(
                            onOrderClick = { orderId -> selectedOrderId = orderId },
                            onBackClick = { currentRoute = "home" }
                        )
                    }
                }
                "restaurant_orders", "deliveries" -> {
                    // TODO: Implement Restaurant Orders/Deliveries Screen
                    PlaceholderScreen("Orders Screen")
                }
                "menu" -> {
                    // TODO: Implement Menu Management Screen
                    PlaceholderScreen("Menu Management")
                }
                "earnings" -> {
                    // TODO: Implement Earnings Screen
                    PlaceholderScreen("Earnings Screen")
                }
                "profile", "restaurant_profile", "rider_profile" -> {
                    ProfileScreen(
                        sessionManager = sessionManager,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "$title\n(Coming Soon)",
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}