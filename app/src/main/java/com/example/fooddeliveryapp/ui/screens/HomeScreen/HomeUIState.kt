package com.example.fooddeliveryapp.ui.screens.HomeScreen


import com.example.fooddeliveryapp.data.model.*

data class HomeUIState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null,

    // Customer specific
    val restaurants: List<Restaurant> = emptyList(),
    val categories: List<FoodCategory> = emptyList(),
    val recentOrders: List<Order> = emptyList(),
    val currentLocation: String = "Current Location",

    // Restaurant specific
    val pendingOrders: List<Order> = emptyList(),
    val isRestaurantOpen: Boolean = true,
    val todayEarnings: Double = 0.0,
    val todayOrdersCount: Int = 0,

    // Rider specific
    val availableDeliveries: List<DeliveryRequest> = emptyList(),
    val isRiderOnline: Boolean = false,
    val todayDeliveries: Int = 0,
    val todayEarningsRider: Double = 0.0,
    val currentDelivery: DeliveryRequest? = null
)

sealed class HomeUIEvent {
    // Common events
    object Refresh : HomeUIEvent()
    data class SearchQueryChanged(val query: String) : HomeUIEvent()
    data class CategorySelected(val category: String?) : HomeUIEvent()
    object ClearError : HomeUIEvent()

    // Customer events
    data class RestaurantClicked(val restaurant: Restaurant) : HomeUIEvent()
    object LocationClicked : HomeUIEvent()

    // Restaurant events
    data class OrderStatusChanged(val orderId: String, val status: OrderStatus) : HomeUIEvent()
    data class RestaurantStatusToggled(val isOpen: Boolean) : HomeUIEvent()
    data class OrderAccepted(val orderId: String) : HomeUIEvent()
    data class OrderRejected(val orderId: String) : HomeUIEvent()

    // Rider events
    data class RiderStatusToggled(val isOnline: Boolean) : HomeUIEvent()
    data class DeliveryAccepted(val deliveryId: String) : HomeUIEvent()
    data class DeliveryCompleted(val deliveryId: String) : HomeUIEvent()
}
