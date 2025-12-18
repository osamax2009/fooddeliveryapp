package com.example.fooddeliveryapp.ui.features.orders

import com.example.fooddeliveryapp.data.model.Order

/**
 * UI state for order screens
 */
data class OrderUIState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val selectedOrder: Order? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isPlacingOrder: Boolean = false,
    val orderPlaced: Boolean = false
)
