package com.example.fooddeliveryapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.ui.features.orders.CheckoutScreen
import com.example.fooddeliveryapp.ui.features.orders.OrderDetailsScreen
import com.example.fooddeliveryapp.ui.features.orders.OrderListScreen
import com.example.fooddeliveryapp.ui.screens.HomeScreen.HomeScreen
import com.example.fooddeliveryapp.ui.screens.auth.AuthScreen
import com.example.fooddeliveryapp.ui.screens.splash.SplashScreen
import javax.inject.Inject

@Composable
fun FoodHubNavigation(
    navController: NavHostController,
    sessionManager: SessionManager // Inject SessionManager here instead
) {
    val isLoggedIn = sessionManager.isLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {
        composable<SplashRoute> {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate(AuthRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(HomeRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                },
                isLoggedIn = isLoggedIn.value
            )
        }

        composable<AuthRoute> {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo(AuthRoute) { inclusive = true }
                    }
                },
                sessionManager = sessionManager // Pass sessionManager
            )
        }

        composable<HomeRoute> {
            HomeScreen(
                onLogout = {
                    navController.navigate(AuthRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                },
                sessionManager = sessionManager // Pass sessionManager
            )
        }

        // Order List Screen
        composable<OrderListRoute> {
            OrderListScreen(
                onOrderClick = { orderId ->
                    navController.navigate(OrderDetailsRoute(orderId))
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        // Order Details Screen
        composable<OrderDetailsRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<OrderDetailsRoute>()
            OrderDetailsScreen(
                orderId = route.orderId,
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        // Checkout Screen
        composable<CheckoutRoute> {
            CheckoutScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onOrderPlaced = { orderId ->
                    navController.navigate(OrderDetailsRoute(orderId)) {
                        popUpTo(CheckoutRoute) { inclusive = true }
                    }
                }
            )
        }
    }
}