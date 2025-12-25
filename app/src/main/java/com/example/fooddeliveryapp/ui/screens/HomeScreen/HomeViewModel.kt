package com.example.fooddeliveryapp.ui.screens.HomeScreen


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.data.repository.DataRepository
import com.example.fooddeliveryapp.data.repository.RiderRepository
import com.example.fooddeliveryapp.data.model.OrderStatus
import com.example.fooddeliveryapp.data.model.Restaurant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val riderRepository: RiderRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun onEvent(event: HomeUIEvent) {
        when (event) {
            is HomeUIEvent.Refresh -> loadInitialData()
            is HomeUIEvent.SearchQueryChanged -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is HomeUIEvent.CategorySelected -> {
                _uiState.value = _uiState.value.copy(selectedCategory = event.category)
            }
            is HomeUIEvent.ClearError -> {
                _uiState.value = _uiState.value.copy(errorMessage = null)
            }

            // Customer events
            is HomeUIEvent.RestaurantClicked -> {
                // Handle restaurant selection
            }
            is HomeUIEvent.LocationClicked -> {
                // Handle location selection
            }

            // Restaurant events
            is HomeUIEvent.OrderStatusChanged -> {
                updateOrderStatus(event.orderId, event.status)
            }
            is HomeUIEvent.RestaurantStatusToggled -> {
                _uiState.value = _uiState.value.copy(isRestaurantOpen = event.isOpen)
            }
            is HomeUIEvent.OrderAccepted -> {
                updateOrderStatus(event.orderId, OrderStatus.CONFIRMED)
            }
            is HomeUIEvent.OrderRejected -> {
                updateOrderStatus(event.orderId, OrderStatus.CANCELLED)
            }

            // Rider events
            is HomeUIEvent.RiderStatusToggled -> {
                _uiState.value = _uiState.value.copy(isRiderOnline = event.isOnline)
                if (event.isOnline) {
                    viewModelScope.launch {
                        updateRiderLocationAndLoad()
                    }
                }
            }
            is HomeUIEvent.DeliveryAccepted -> {
                acceptDelivery(event.deliveryId)
            }
            is HomeUIEvent.DeliveryRejected -> {
                rejectDelivery(event.deliveryId)
            }
            is HomeUIEvent.DeliveryCompleted -> {
                completeDelivery(event.deliveryId)
            }
        }
    }

    private fun loadInitialData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                when (sessionManager.getUserType()) {
                    SessionManager.UserType.CUSTOMER -> loadCustomerData()
                    SessionManager.UserType.RESTAURANT -> loadRestaurantData()
                    SessionManager.UserType.RIDER -> updateRiderLocationAndLoad()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load data: ${e.message}"
                )
            }
        }
    }

    private suspend fun loadCustomerData() {
        val restaurantsResult = dataRepository.getRestaurants()
        val categoriesResult = dataRepository.getFoodCategories()
        val ordersResult = dataRepository.getUserOrders()

        if (restaurantsResult.isSuccess && categoriesResult.isSuccess && ordersResult.isSuccess) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                restaurants = restaurantsResult.getOrNull() ?: emptyList(),
                categories = categoriesResult.getOrNull() ?: emptyList(),
                recentOrders = ordersResult.getOrNull() ?: emptyList()
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Failed to load restaurant data"
            )
        }
    }

    private suspend fun loadRestaurantData() {
        val ordersResult = dataRepository.getRestaurantOrders()

        if (ordersResult.isSuccess) {
            val orders = ordersResult.getOrNull() ?: emptyList()
            val pendingOrders = orders.filter { it.status in listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.PREPARING) }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                pendingOrders = pendingOrders,
                todayOrdersCount = orders.size,
                todayEarnings = orders.sumOf { it.totalAmount }
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Failed to load order data"
            )
        }
    }

    /**
     * Update rider location before loading deliveries
     * Location must be updated before the backend will show available deliveries
     */
    private suspend fun updateRiderLocationAndLoad() {
        Log.d(TAG, "Updating rider location before loading deliveries...")

        // TODO: Get actual device location using FusedLocationProviderClient
        // For now using mock location data
        val mockLatitude = 37.7749 // San Francisco
        val mockLongitude = -122.4194
        val mockAddress = "San Francisco, CA"

        val locationResult = riderRepository.updateLocation(
            latitude = mockLatitude,
            longitude = mockLongitude,
            address = mockAddress
        )

        if (locationResult.isSuccess) {
            Log.d(TAG, "Location updated successfully, now loading deliveries")
            loadRiderData()
        } else {
            val error = locationResult.exceptionOrNull()?.message ?: "Failed to update location"
            Log.e(TAG, "Failed to update location: $error")
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Please enable location services to view available deliveries"
            )
        }
    }

    private suspend fun loadRiderData() {
        Log.d(TAG, "Loading rider data...")

        // Fetch both available and active deliveries
        val availableResult = riderRepository.getAvailableDeliveries()
        val activeResult = riderRepository.getActiveDeliveries()

        // Handle partial success - show what we can even if one endpoint fails
        val availableDeliveries = availableResult.getOrNull() ?: emptyList()
        val activeDeliveries = activeResult.getOrNull() ?: emptyList()

        // Convert orders to DeliveryRequest format
        val availableRequests = availableDeliveries.map { it.toDeliveryRequest() }
        val currentDelivery = activeDeliveries.firstOrNull()?.toDeliveryRequest()

        Log.d(TAG, "Loaded ${availableRequests.size} available deliveries, ${activeDeliveries.size} active deliveries")

        // Build error message if any endpoint failed
        val errorMessage = when {
            availableResult.isFailure && activeResult.isFailure -> {
                "Failed to load deliveries: ${availableResult.exceptionOrNull()?.message ?: "Unknown error"}"
            }
            availableResult.isFailure -> {
                Log.w(TAG, "Available deliveries failed: ${availableResult.exceptionOrNull()?.message}")
                "Could not load available deliveries. Showing active deliveries only."
            }
            activeResult.isFailure -> {
                Log.w(TAG, "Active deliveries failed: ${activeResult.exceptionOrNull()?.message}")
                "Could not load active deliveries. Showing available deliveries only."
            }
            else -> null
        }

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            availableDeliveries = availableRequests,
            currentDelivery = currentDelivery,
            errorMessage = errorMessage
        )
    }

    private fun updateOrderStatus(orderId: String, status: OrderStatus) {
        val currentOrders = _uiState.value.pendingOrders
        val updatedOrders = currentOrders.map { order ->
            if (order.id == orderId) {
                order.copy(status = status)
            } else {
                order
            }
        }.filter { it.status != OrderStatus.CANCELLED } // Remove cancelled orders

        _uiState.value = _uiState.value.copy(pendingOrders = updatedOrders)
    }

    private fun acceptDelivery(deliveryId: String) {
        Log.d(TAG, "Accepting delivery: $deliveryId")

        viewModelScope.launch {
            try {
                val result = riderRepository.acceptDelivery(deliveryId)

                if (result.isSuccess) {
                    Log.d(TAG, "Successfully accepted delivery: $deliveryId")
                    // Reload rider data to refresh the lists
                    loadRiderData()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Failed to accept delivery"
                    Log.e(TAG, "Failed to accept delivery: $error")
                    _uiState.value = _uiState.value.copy(errorMessage = error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while accepting delivery: ${e.message}", e)
                _uiState.value = _uiState.value.copy(errorMessage = "Error: ${e.message}")
            }
        }
    }

    private fun rejectDelivery(deliveryId: String) {
        Log.d(TAG, "Rejecting delivery: $deliveryId")

        viewModelScope.launch {
            try {
                val result = riderRepository.rejectDelivery(deliveryId)

                if (result.isSuccess) {
                    Log.d(TAG, "Successfully rejected delivery: $deliveryId")
                    // Remove from available deliveries
                    val updatedDeliveries = _uiState.value.availableDeliveries.filter { it.id != deliveryId }
                    _uiState.value = _uiState.value.copy(availableDeliveries = updatedDeliveries)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Failed to reject delivery"
                    Log.e(TAG, "Failed to reject delivery: $error")
                    _uiState.value = _uiState.value.copy(errorMessage = error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while rejecting delivery: ${e.message}", e)
                _uiState.value = _uiState.value.copy(errorMessage = "Error: ${e.message}")
            }
        }
    }

    private fun completeDelivery(deliveryId: String) {
        Log.d(TAG, "Completing delivery: $deliveryId")

        viewModelScope.launch {
            try {
                val result = riderRepository.updateDeliveryStatus(deliveryId, "DELIVERED")

                if (result.isSuccess) {
                    Log.d(TAG, "Successfully completed delivery: $deliveryId")
                    val earnings = _uiState.value.currentDelivery?.estimatedEarnings ?: 0.0
                    _uiState.value = _uiState.value.copy(
                        currentDelivery = null,
                        todayDeliveries = _uiState.value.todayDeliveries + 1,
                        todayEarningsRider = _uiState.value.todayEarningsRider + earnings
                    )
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Failed to complete delivery"
                    Log.e(TAG, "Failed to complete delivery: $error")
                    _uiState.value = _uiState.value.copy(errorMessage = error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while completing delivery: ${e.message}", e)
                _uiState.value = _uiState.value.copy(errorMessage = "Error: ${e.message}")
            }
        }
    }

    // Filtered data for UI
    fun getFilteredRestaurants(): List<Restaurant> {
        val restaurants = _uiState.value.restaurants
        val query = _uiState.value.searchQuery
        val category = _uiState.value.selectedCategory

        return restaurants.filter { restaurant ->
            val matchesQuery = if (query.isBlank()) true else
                restaurant.name.contains(query, ignoreCase = true) ||
                        restaurant.description.contains(query, ignoreCase = true)

            val matchesCategory = if (category == null) true else
                restaurant.categories.contains(category)

            matchesQuery && matchesCategory
        }
    }
}

/**
 * Extension function to convert Order to DeliveryRequest
 */
private fun com.example.fooddeliveryapp.data.model.Order.toDeliveryRequest(): com.example.fooddeliveryapp.data.model.DeliveryRequest {
    return com.example.fooddeliveryapp.data.model.DeliveryRequest(
        id = this.id,
        orderNumber = this.id.takeLast(8).uppercase(), // Use last 8 chars of ID as order number
        restaurantName = this.restaurantName,
        customerName = this.deliveryAddress?.split(",")?.firstOrNull() ?: "Customer",
        pickupAddress = this.restaurantName, // Using restaurant name as pickup address for now
        deliveryAddress = this.deliveryAddress ?: "Unknown address",
        distance = "2.5 km", // TODO: Calculate actual distance
        estimatedEarnings = this.totalAmount * 0.15, // 15% of order total as earnings
        items = this.items,
        specialInstructions = null
    )
}