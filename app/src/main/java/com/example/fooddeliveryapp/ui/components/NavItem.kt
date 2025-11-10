package com.example.fooddeliveryapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val badgeCount: Int? = null
) {
    // Customer nav items
    object Home : NavItem("home", Icons.Default.Home, "Home")
    object Search : NavItem("search", Icons.Default.Search, "Search")
    object Orders : NavItem("orders", Icons.Default.ShoppingCart, "Orders")
    object Profile : NavItem("profile", Icons.Default.AccountCircle, "Profile")

    // Restaurant nav items
    object Dashboard : NavItem("dashboard", Icons.Default.Home, "Dashboard")
    object Menu : NavItem("menu", Icons.Default.Restaurant, "Menu")
    object RestaurantOrders : NavItem("restaurant_orders", Icons.Default.Assignment, "Orders")
    object RestaurantProfile : NavItem("restaurant_profile", Icons.Default.AccountCircle, "Profile")

    // Rider nav items
    object RiderHome : NavItem("rider_home", Icons.Default.Home, "Home")
    object Deliveries : NavItem("deliveries", Icons.Default.DeliveryDining, "Deliveries")
    object Earnings : NavItem("earnings", Icons.Default.Assignment, "Earnings")
    object RiderProfile : NavItem("rider_profile", Icons.Default.AccountCircle, "Profile")

    // Dynamic nav items with badge counts
    class OrdersWithBadge(badgeCount: Int?) : NavItem("orders", Icons.Default.ShoppingCart, "Orders", badgeCount)
    class RestaurantOrdersWithBadge(badgeCount: Int?) : NavItem("restaurant_orders", Icons.Default.Assignment, "Orders", badgeCount)
}
