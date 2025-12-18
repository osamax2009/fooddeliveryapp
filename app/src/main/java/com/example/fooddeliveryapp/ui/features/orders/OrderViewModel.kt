package com.example.fooddeliveryapp.ui.features.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing order operations
 */
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUIState())
    val uiState: StateFlow<OrderUIState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun onEvent(event: OrderUIEvent) {
        when (event) {
            is OrderUIEvent.LoadOrders -> loadOrders()
            is OrderUIEvent.LoadOrderDetails -> loadOrderDetails(event.orderId)
            is OrderUIEvent.PlaceOrder -> placeOrder(event.addressId)
            is OrderUIEvent.UpdateOrderStatus -> updateOrderStatus(event.orderId, event.status)
            is OrderUIEvent.ClearError -> {
                _uiState.value = _uiState.value.copy(errorMessage = null)
            }
            is OrderUIEvent.ClearSuccess -> {
                _uiState.value = _uiState.value.copy(successMessage = null)
            }
            is OrderUIEvent.ResetOrderPlaced -> {
                _uiState.value = _uiState.value.copy(orderPlaced = false)
            }
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            orderRepository.getUserOrders()
                .onSuccess { orders ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        orders = orders,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load orders"
                    )
                }
        }
    }

    private fun loadOrderDetails(orderId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            orderRepository.getOrderDetails(orderId)
                .onSuccess { order ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedOrder = order,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load order details"
                    )
                }
        }
    }

    private fun placeOrder(addressId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isPlacingOrder = true,
                errorMessage = null,
                orderPlaced = false
            )

            orderRepository.placeOrder(addressId)
                .onSuccess { order ->
                    _uiState.value = _uiState.value.copy(
                        isPlacingOrder = false,
                        orderPlaced = true,
                        selectedOrder = order,
                        successMessage = "Order placed successfully!",
                        errorMessage = null
                    )
                    // Refresh orders list
                    loadOrders()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isPlacingOrder = false,
                        orderPlaced = false,
                        errorMessage = error.message ?: "Failed to place order"
                    )
                }
        }
    }

    private fun updateOrderStatus(orderId: String, status: com.example.fooddeliveryapp.data.model.OrderStatus) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            orderRepository.updateOrderStatus(orderId, status)
                .onSuccess { updatedOrder ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedOrder = updatedOrder,
                        successMessage = "Order status updated successfully",
                        errorMessage = null
                    )
                    // Refresh orders list
                    loadOrders()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to update order status"
                    )
                }
        }
    }
}
