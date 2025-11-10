package com.example.fooddeliveryapp.ui.components

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.fooddeliveryapp.data.SessionManager


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