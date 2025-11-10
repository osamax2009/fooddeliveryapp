package com.example.fooddeliveryapp.data.repository


import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.data.api.RestaurantApiService
import com.example.fooddeliveryapp.data.model.*
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(
    private val restaurantApiService: RestaurantApiService,
    private val sessionManager: SessionManager
) {

    suspend fun getRestaurants(): Result<List<Restaurant>> {
        return try {
            val token = sessionManager.getToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No authentication token found"))
            }

            // TODO: Get actual user location from location service
            // Using default coordinates for now (New York)
            val latitude = 40.712776
            val longitude = -74.005978

            val response = restaurantApiService.getRestaurants(
                latitude = latitude,
                longitude = longitude,
                authorization = "Bearer $token"
            )

            if (response.isSuccessful) {
                val restaurantsResponse = response.body()
                if (restaurantsResponse != null) {
                    val restaurants = restaurantsResponse.data.map { it.toDomainModel() }
                    Result.success(restaurants)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                Result.failure(Exception("Failed to fetch restaurants: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFoodCategories(): Result<List<FoodCategory>> {
        delay(500)
        return Result.success(mockCategories)
    }

    suspend fun getRestaurantOrders(): Result<List<Order>> {
        delay(800)
        return Result.success(mockRestaurantOrders)
    }

    suspend fun getDeliveryRequests(): Result<List<DeliveryRequest>> {
        delay(600)
        return Result.success(mockDeliveryRequests)
    }

    suspend fun getUserOrders(): Result<List<Order>> {
        delay(700)
        return Result.success(mockUserOrders)
    }

    // Mock Data
    private val mockCategories = listOf(
        FoodCategory("1", "Pizza", "🍕", "#FF6B47"),
        FoodCategory("2", "Burger", "🍔", "#4CAF50"),
        FoodCategory("3", "Asian", "🍜", "#FF9800"),
        FoodCategory("4", "Dessert", "🍰", "#E91E63"),
        FoodCategory("5", "Healthy", "🥗", "#8BC34A"),
        FoodCategory("6", "Coffee", "☕", "#795548")
    )

    private val mockRestaurants = listOf(
        Restaurant(
            id = "1",
            name = "Pizza Palace",
            description = "Authentic Italian pizza with fresh ingredients",
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400",
            rating = 4.5f,
            deliveryTime = "25-35 min",
            deliveryFee = 2.99,
            categories = listOf("Pizza", "Italian")
        ),
        Restaurant(
            id = "2",
            name = "Burger Haven",
            description = "Gourmet burgers and crispy fries",
            imageUrl = "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400",
            rating = 4.3f,
            deliveryTime = "20-30 min",
            deliveryFee = 1.99,
            categories = listOf("Burger", "American")
        ),
        Restaurant(
            id = "3",
            name = "Sushi Zen",
            description = "Fresh sushi and Japanese cuisine",
            imageUrl = "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=400",
            rating = 4.7f,
            deliveryTime = "30-40 min",
            deliveryFee = 3.49,
            categories = listOf("Asian", "Sushi")
        ),
        Restaurant(
            id = "4",
            name = "Healthy Bites",
            description = "Nutritious meals and fresh salads",
            imageUrl = "https://images.unsplash.com/photo-1551504734-5ee1c4a1479b?w=400",
            rating = 4.2f,
            deliveryTime = "15-25 min",
            deliveryFee = 1.49,
            categories = listOf("Healthy", "Salads")
        ),
        Restaurant(
            id = "5",
            name = "Taco Fiesta",
            description = "Authentic Mexican street food",
            imageUrl = "https://unsplash.com/photos/taco-pizza-ZQf4jzkpz1k",
            rating = 4.4f,
            deliveryTime = "20-30 min",
            deliveryFee = 2.49,
            categories = listOf("Mexican", "Tacos")
        ),
        Restaurant(
            id = "6",
            name = "Café Central",
            description = "Premium coffee and pastries",
            imageUrl = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400",
            rating = 4.6f,
            deliveryTime = "10-20 min",
            deliveryFee = 0.99,
            categories = listOf("Coffee", "Pastries")
        )
    )

    private val mockMenuItems = listOf(
        MenuItem("1", "Margherita Pizza", "Classic tomato sauce, mozzarella, basil", 12.99, "https://images.unsplash.com/photo-1604382355076-af4b0eb60143?w=300", "Pizza"),
        MenuItem("2", "Classic Burger", "Beef patty, lettuce, tomato, onion", 9.99, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300", "Burger"),
        MenuItem("3", "Salmon Roll", "Fresh salmon, avocado, cucumber", 8.99, "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=300", "Sushi")
    )

    private val mockRestaurantOrders = listOf(
        Order(
            id = "R001",
            restaurantName = "Pizza Palace",
            items = listOf(OrderItem(mockMenuItems[0], 2)),
            status = OrderStatus.PENDING,
            totalAmount = 25.98,
            createdAt = "2 min ago",
            estimatedDelivery = "25-35 min"
        ),
        Order(
            id = "R002",
            restaurantName = "Pizza Palace",
            items = listOf(OrderItem(mockMenuItems[0], 1), OrderItem(mockMenuItems[1], 1)),
            status = OrderStatus.PREPARING,
            totalAmount = 22.98,
            createdAt = "8 min ago",
            estimatedDelivery = "20-30 min"
        ),
        Order(
            id = "R003",
            restaurantName = "Pizza Palace",
            items = listOf(OrderItem(mockMenuItems[2], 3)),
            status = OrderStatus.READY_FOR_PICKUP,
            totalAmount = 26.97,
            createdAt = "15 min ago"
        )
    )

    private val mockDeliveryRequests = listOf(
        DeliveryRequest(
            id = "D001",
            orderNumber = "ORD-001",
            restaurantName = "Pizza Palace",
            customerName = "John Doe",
            pickupAddress = "123 Restaurant St",
            deliveryAddress = "456 Customer Ave",
            distance = "2.3 km",
            estimatedEarnings = 8.50,
            items = listOf(OrderItem(mockMenuItems[0], 2))
        ),
        DeliveryRequest(
            id = "D002",
            orderNumber = "ORD-002",
            restaurantName = "Burger Haven",
            customerName = "Jane Smith",
            pickupAddress = "789 Burger Blvd",
            deliveryAddress = "321 Home St",
            distance = "1.8 km",
            estimatedEarnings = 6.75,
            items = listOf(OrderItem(mockMenuItems[1], 1))
        )
    )

    private val mockUserOrders = listOf(
        Order(
            id = "U001",
            restaurantName = "Pizza Palace",
            items = listOf(OrderItem(mockMenuItems[0], 1)),
            status = OrderStatus.OUT_FOR_DELIVERY,
            totalAmount = 15.98,
            createdAt = "30 min ago",
            estimatedDelivery = "10 min",
            deliveryAddress = "123 My Street"
        ),
        Order(
            id = "U002",
            restaurantName = "Sushi Zen",
            items = listOf(OrderItem(mockMenuItems[2], 2)),
            status = OrderStatus.DELIVERED,
            totalAmount = 19.98,
            createdAt = "2 days ago",
            deliveryAddress = "123 My Street"
        )
    )
}