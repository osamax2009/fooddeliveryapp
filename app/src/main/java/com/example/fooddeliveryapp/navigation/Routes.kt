package com.example.fooddeliveryapp.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashRoute

@Serializable
object AuthRoute

@Serializable
object HomeRoute

@Serializable
object OrderListRoute

@Serializable
data class OrderDetailsRoute(val orderId: String)

@Serializable
object CheckoutRoute

@Serializable
data class RestaurantDetailsRoute(val restaurantId: String)

@Serializable
object CartRoute