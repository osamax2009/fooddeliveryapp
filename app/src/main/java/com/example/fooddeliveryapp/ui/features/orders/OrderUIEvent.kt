package com.example.fooddeliveryapp.ui.features.orders

import com.example.fooddeliveryapp.data.model.OrderStatus

/**
 * UI events for order screens
 */
sealed class OrderUIEvent {
    data object LoadOrders : OrderUIEvent()
    data class LoadOrderDetails(val orderId: String) : OrderUIEvent()
    data class PlaceOrder(val addressId: String) : OrderUIEvent()
    data class UpdateOrderStatus(val orderId: String, val status: OrderStatus) : OrderUIEvent()
    data object ClearError : OrderUIEvent()
    data object ClearSuccess : OrderUIEvent()
    data object ResetOrderPlaced : OrderUIEvent()
}
