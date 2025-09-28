package com.example.fooddeliveryapp.ui.screens.HomeScreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.data.repository.DataRepository
import com.example.fooddeliveryapp.data.model.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

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
            }
            is HomeUIEvent.DeliveryAccepted -> {
                acceptDelivery(event.deliveryId)
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
                    SessionManager.UserType.RIDER -> loadRiderData()
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

    private suspend fun loadRiderData() {
        val deliveriesResult = dataRepository.getDeliveryRequests()

        if (deliveriesResult.isSuccess) {
            val deliveries = deliveriesResult.getOrNull() ?: emptyList()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                availableDeliveries = deliveries,
                todayDeliveries = 8, // Mock data
                todayEarningsRider = 67.50 // Mock data
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Failed to load delivery data"
            )
        }
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
        val deliveries = _uiState.value.availableDeliveries
        val selectedDelivery = deliveries.find { it.id == deliveryId }

        selectedDelivery?.let { delivery ->
            _uiState.value = _uiState.value.copy(
                currentDelivery = delivery,
                availableDeliveries = deliveries.filter { it.id != deliveryId }
            )
        }
    }

    private fun completeDelivery(deliveryId: String) {
        _uiState.value = _uiState.value.copy(
            currentDelivery = null,
            todayDeliveries = _uiState.value.todayDeliveries + 1,
            todayEarningsRider = _uiState.value.todayEarningsRider + (_uiState.value.currentDelivery?.estimatedEarnings ?: 0.0)
        )
    }

    // Filtered data for UI
    fun getFilteredRestaurants(): List<com.example.fooddeliveryapp.data.model.Restaurant> {
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