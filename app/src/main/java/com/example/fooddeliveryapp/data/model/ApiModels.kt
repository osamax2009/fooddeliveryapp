package com.example.fooddeliveryapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * API response model for restaurant data
 */
data class RestaurantResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("ownerId")
    val ownerId: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("categoryId")
    val categoryId: String?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("distance")
    val distance: Double?
)

/**
 * API response wrapper for restaurant list
 */
data class RestaurantsApiResponse(
    @SerializedName("data")
    val data: List<RestaurantResponse>
)

/**
 * Maps API response to domain model
 */
fun RestaurantResponse.toDomainModel(): Restaurant {
    return Restaurant(
        id = id ?: "",
        name = name ?: "Unknown Restaurant",
        description = address ?: "No address provided",
        imageUrl = imageUrl ?: "",
        rating = 4.5f, // Default rating, should come from reviews API later
        deliveryTime = "20-30 min", // Default delivery time
        deliveryFee = 2.99, // Default delivery fee
        categories = listOfNotNull(categoryId), // Using categoryId, can be mapped to category names later
        isOpen = true, // Default to open
        distance = String.format("%.1f km", (distance ?: 0.0) * 111.0) // Convert degrees to km (approximate)
    )
}

/**
 * API request model for placing an order
 */
data class PlaceOrderRequest(
    @SerializedName("addressId")
    val addressId: String
)

/**
 * API request model for updating order status
 */
data class UpdateOrderStatusRequest(
    @SerializedName("status")
    val status: String
)

/**
 * API response model for menu item
 */
data class MenuItemResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("restaurantId")
    val restaurantId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("arModelUrl")
    val arModelUrl: String?
)

/**
 * API response model for order item
 */
data class OrderItemResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("orderId")
    val orderId: String,
    @SerializedName("menuItemId")
    val menuItemId: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Double,
    @SerializedName("menuItem")
    val menuItem: MenuItemResponse?
)

/**
 * API response model for address
 */
data class AddressResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("addressLine1")
    val addressLine1: String,
    @SerializedName("addressLine2")
    val addressLine2: String?,
    @SerializedName("city")
    val city: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("zipCode")
    val zipCode: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)

/**
 * API response model for order
 */
data class OrderResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("restaurantId")
    val restaurantId: String,
    @SerializedName("addressId")
    val addressId: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("totalAmount")
    val totalAmount: Double,
    @SerializedName("deliveryFee")
    val deliveryFee: Double,
    @SerializedName("subtotal")
    val subtotal: Double,
    @SerializedName("tax")
    val tax: Double,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("items")
    val items: List<OrderItemResponse>,
    @SerializedName("restaurant")
    val restaurant: RestaurantResponse?,
    @SerializedName("address")
    val address: AddressResponse?
)

/**
 * API response wrapper for order list
 */
data class OrdersApiResponse(
    @SerializedName("data")
    val data: List<OrderResponse>
)

/**
 * API response wrapper for single order
 */
data class OrderApiResponse(
    @SerializedName("data")
    val data: OrderResponse
)

/**
 * Maps order status string to OrderStatus enum
 */
fun String.toOrderStatus(): OrderStatus {
    return when (this.uppercase()) {
        "PENDING_ACCEPTANCE", "PENDING" -> OrderStatus.PENDING
        "ACCEPTED", "CONFIRMED" -> OrderStatus.CONFIRMED
        "PREPARING" -> OrderStatus.PREPARING
        "READY", "READY_FOR_PICKUP" -> OrderStatus.READY_FOR_PICKUP
        "ASSIGNED", "OUT_FOR_DELIVERY" -> OrderStatus.OUT_FOR_DELIVERY
        "DELIVERED" -> OrderStatus.DELIVERED
        "REJECTED", "CANCELLED", "DELIVERY_FAILED" -> OrderStatus.CANCELLED
        else -> OrderStatus.PENDING
    }
}

/**
 * Maps MenuItemResponse to MenuItem domain model
 */
fun MenuItemResponse.toDomainModel(): MenuItem {
    return MenuItem(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl ?: "",
        category = "", // Category is not included in the response
        isVegetarian = false // Not included in API response
    )
}

/**
 * Maps OrderResponse to Order domain model
 */
fun OrderResponse.toDomainModel(): Order {
    return Order(
        id = id,
        restaurantName = restaurant?.name ?: "Unknown Restaurant",
        items = items.map { orderItem ->
            OrderItem(
                menuItem = orderItem.menuItem?.toDomainModel() ?: MenuItem(
                    id = orderItem.menuItemId,
                    name = "Unknown Item",
                    description = "",
                    price = orderItem.price,
                    imageUrl = "",
                    category = ""
                ),
                quantity = orderItem.quantity
            )
        },
        status = status.toOrderStatus(),
        totalAmount = totalAmount,
        createdAt = createdAt,
        estimatedDelivery = null, // Not included in API response
        deliveryAddress = address?.let { "${it.addressLine1}, ${it.city}, ${it.state} ${it.zipCode}" }
    )
}

/**
 * API request model for adding item to cart
 */
data class AddToCartRequest(
    @SerializedName("restaurantId")
    val restaurantId: String,
    @SerializedName("menuItemId")
    val menuItemId: String,
    @SerializedName("quantity")
    val quantity: Int
)

/**
 * API request model for updating cart item quantity
 */
data class UpdateCartItemRequest(
    @SerializedName("cartItemId")
    val cartItemId: String,
    @SerializedName("quantity")
    val quantity: Int
)

/**
 * API response model for cart item
 */
data class CartItemResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("restaurantId")
    val restaurantId: String,
    @SerializedName("menuItemId")
    val menuItemId: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("menuItem")
    val menuItem: MenuItemResponse?
)

/**
 * API response model for cart with checkout details
 */
data class CartResponse(
    @SerializedName("items")
    val items: List<CartItemResponse>?,
    @SerializedName("subtotal")
    val subtotal: Double?,
    @SerializedName("tax")
    val tax: Double?,
    @SerializedName("deliveryFee")
    val deliveryFee: Double?,
    @SerializedName("total")
    val total: Double?,
    @SerializedName("restaurantId")
    val restaurantId: String?
)

/**
 * API response wrapper for cart
 */
data class CartApiResponse(
    @SerializedName("data")
    val data: CartResponse?
)

/**
 * API response wrapper for menu items list
 */
data class MenuItemsApiResponse(
    @SerializedName("data")
    val data: List<MenuItemResponse?>?
)
