package com.example.fooddeliveryapp.ui.features.restaurant

sealed class RestaurantDetailUIEvent {
    data class LoadRestaurantMenu(val restaurantId: String) : RestaurantDetailUIEvent()
    data class AddToCart(val menuItemId: String, val quantity: Int) : RestaurantDetailUIEvent()
    data object ClearError : RestaurantDetailUIEvent()
    data object ClearSuccess : RestaurantDetailUIEvent()
}
