package com.example.fooddeliveryapp.ui.features.restaurant

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.data.model.Restaurant
import com.example.fooddeliveryapp.data.repository.CartRepository
import com.example.fooddeliveryapp.data.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    companion object {
        private const val TAG = "RestaurantDetailVM"
    }

    private val _uiState = MutableStateFlow(RestaurantDetailUIState())
    val uiState: StateFlow<RestaurantDetailUIState> = _uiState.asStateFlow()

    fun onEvent(event: RestaurantDetailUIEvent) {
        when (event) {
            is RestaurantDetailUIEvent.LoadRestaurantMenu -> loadRestaurantMenu(event.restaurantId)
            is RestaurantDetailUIEvent.AddToCart -> addToCart(event.menuItemId, event.quantity)
            is RestaurantDetailUIEvent.ClearError -> {
                _uiState.value = _uiState.value.copy(errorMessage = null)
            }
            is RestaurantDetailUIEvent.ClearSuccess -> {
                _uiState.value = _uiState.value.copy(successMessage = null)
            }
        }
    }

    fun setRestaurant(restaurant: Restaurant) {
        Log.d(TAG, "setRestaurant: ${restaurant.name} (id=${restaurant.id})")
        _uiState.value = _uiState.value.copy(restaurant = restaurant)
        loadRestaurantMenu(restaurant.id)
        loadCartItemCount()
    }

    private fun loadRestaurantMenu(restaurantId: String) {
        viewModelScope.launch {
            Log.d(TAG, "loadRestaurantMenu: Loading menu for restaurantId=$restaurantId")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            dataRepository.getRestaurantMenu(restaurantId)
                .onSuccess { menuItems ->
                    Log.d(TAG, "loadRestaurantMenu: Success - ${menuItems.size} items")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        menuItems = menuItems,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "loadRestaurantMenu: Failed - ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load menu"
                    )
                }
        }
    }

    private fun addToCart(menuItemId: String, quantity: Int) {
        viewModelScope.launch {
            Log.d(TAG, "addToCart: menuItemId=$menuItemId, quantity=$quantity")
            val restaurantId = _uiState.value.restaurant?.id
            if (restaurantId == null) {
                Log.e(TAG, "addToCart: Restaurant not found")
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Restaurant not found"
                )
                return@launch
            }

            cartRepository.addToCart(restaurantId, menuItemId, quantity)
                .onSuccess { cartResponse ->
                    Log.d(TAG, "addToCart: Success - cart has ${cartResponse?.items?.size ?: 0} items")
                    _uiState.value = _uiState.value.copy(
                        cartItemCount = cartResponse?.items?.size ?: 0,
                        successMessage = "Added to cart!",
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "addToCart: Failed - ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message ?: "Failed to add to cart"
                    )
                }
        }
    }

    private fun loadCartItemCount() {
        viewModelScope.launch {
            Log.d(TAG, "loadCartItemCount: Loading cart count")
            cartRepository.getCart()
                .onSuccess { cartResponse ->
                    val count = cartResponse?.items?.size ?: 0
                    Log.d(TAG, "loadCartItemCount: Success - $count items in cart")
                    _uiState.value = _uiState.value.copy(cartItemCount = count)
                }
                .onFailure { error ->
                    Log.w(TAG, "loadCartItemCount: Failed - ${error.message}, setting count to 0")
                    // Ignore cart load failures, just set count to 0
                    _uiState.value = _uiState.value.copy(cartItemCount = 0)
                }
        }
    }
}
