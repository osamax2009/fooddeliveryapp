package com.example.fooddeliveryapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.fooddeliveryapp.data.model.UserData
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("foodhub_session", Context.MODE_PRIVATE)

    private val gson = Gson()

    // Session state
    private val _isLoggedIn = MutableStateFlow(hasValidToken())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userType = MutableStateFlow(getUserType())
    val userType: StateFlow<UserType> = _userType.asStateFlow()

    enum class UserType { CUSTOMER, RESTAURANT, RIDER }

    fun storeToken(token: String?) {
        if (token.isNullOrBlank()) {
            throw IllegalArgumentException("Token cannot be null or empty")
        }
        prefs.edit().putString("auth_token", token).apply()
        _isLoggedIn.value = true
    }

    fun storeUserData(userData: UserData?) {
        val userDataJson = gson.toJson(userData)
        prefs.edit().putString("user_data", userDataJson).apply()

        // Store user type based on role
        val userType = when (userData?.role?.lowercase()) {
            "owner" -> UserType.RESTAURANT
            "rider" -> UserType.RIDER
            else -> UserType.CUSTOMER
        }
        storeUserType(userType.name)
    }

    fun getUserData(): UserData? {
        val userDataJson = prefs.getString("user_data", null)
        return if (userDataJson != null) {
            try {
                gson.fromJson(userDataJson, UserData::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    fun storeUserType(userType: String) {
        prefs.edit().putString("user_type", userType).apply()
        _userType.value = UserType.valueOf(userType)
    }

    fun getToken(): String? = prefs.getString("auth_token", null)

    fun getUserType(): UserType {
        // First check if user has manually selected a type during login
        val storedType = prefs.getString("user_type", null)
        if (storedType != null) {
            return try {
                UserType.valueOf(storedType)
            } catch (e: IllegalArgumentException) {
                getDefaultUserTypeFromPackage()
            }
        }

        // Fallback to package name detection
        return getDefaultUserTypeFromPackage()
    }

    private fun getDefaultUserTypeFromPackage(): UserType {
        val packageName = context.packageName
        return when {
            packageName.endsWith(".restaurant") -> UserType.RESTAURANT
            packageName.endsWith(".rider") -> UserType.RIDER
            else -> UserType.CUSTOMER
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        _isLoggedIn.value = false
        _userType.value = getDefaultUserTypeFromPackage()
    }

    private fun hasValidToken(): Boolean = !getToken().isNullOrEmpty()
}