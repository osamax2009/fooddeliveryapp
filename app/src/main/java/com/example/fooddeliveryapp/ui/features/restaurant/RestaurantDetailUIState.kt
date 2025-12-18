package com.example.fooddeliveryapp.ui.features.restaurant

import com.example.fooddeliveryapp.data.model.MenuItem
import com.example.fooddeliveryapp.data.model.Restaurant

data class RestaurantDetailUIState(
    val isLoading: Boolean = false,
    val restaurant: Restaurant? = null,
    val menuItems: List<MenuItem> = emptyList(),
    val cartItemCount: Int = 0,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
