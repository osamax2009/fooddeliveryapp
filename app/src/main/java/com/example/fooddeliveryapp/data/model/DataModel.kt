package com.example.fooddeliveryapp.data.model


data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rating: Float,
    val deliveryTime: String,
    val deliveryFee: Double,
    val categories: List<String>,
    val isOpen: Boolean = true,
    val distance: String = "1.2 km"
)

data class FoodCategory(
    val id: String,
    val name: String,
    val iconUrl: String,
    val color: String = "#FF6B47"
)

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val isVegetarian: Boolean = false
)

data class Order(
    val id: String,
    val restaurantName: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val totalAmount: Double,
    val createdAt: String,
    val estimatedDelivery: String? = null,
    val deliveryAddress: String? = null
)

data class OrderItem(
    val menuItem: MenuItem,
    val quantity: Int,
    val specialInstructions: String? = null
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}

data class DeliveryRequest(
    val id: String,
    val orderNumber: String,
    val restaurantName: String,
    val customerName: String,
    val pickupAddress: String,
    val deliveryAddress: String,
    val distance: String,
    val estimatedEarnings: Double,
    val items: List<OrderItem>,
    val specialInstructions: String? = null
)
