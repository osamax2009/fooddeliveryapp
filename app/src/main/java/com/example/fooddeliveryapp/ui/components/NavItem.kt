package com.example.fooddeliveryapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fooddeliveryapp.data.SessionManager

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

@Composable
fun BottomNavigationBar(
    userType: SessionManager.UserType,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    pendingOrdersCount: Int = 0
) {
    val navItems = when (userType) {
        SessionManager.UserType.CUSTOMER -> listOf(
            NavItem.Home,
            NavItem.Search,
            NavItem.OrdersWithBadge(if (pendingOrdersCount > 0) pendingOrdersCount else null),
            NavItem.Profile
        )
        SessionManager.UserType.RESTAURANT -> listOf(
            NavItem.Dashboard,
            NavItem.Menu,
            NavItem.RestaurantOrdersWithBadge(if (pendingOrdersCount > 0) pendingOrdersCount else null),
            NavItem.RestaurantProfile
        )
        SessionManager.UserType.RIDER -> listOf(
            NavItem.RiderHome,
            NavItem.Deliveries,
            NavItem.Earnings,
            NavItem.RiderProfile
        )
    }

    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = {
                            item.badgeCount?.let { count ->
                                Badge {
                                    Text(count.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    }
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}