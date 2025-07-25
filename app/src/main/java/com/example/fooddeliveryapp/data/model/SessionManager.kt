package com.example.fooddeliveryapp.data.model

import android.content.Context
import com.stripe.android.customersheet.injection.CustomerSheetViewModelModule_Companion_ContextFactory.context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SessionManager @Inject constructor(
    private val context : Context
){
    private val prefs =
        context.getSharedPreferences("session", Context.MODE_PRIVATE)
    private val _userType = MutableStateFlow(getUserType())
    val userType = _userType.asStateFlow()
    private val _isLoggedIn = MutableStateFlow(hasValidToken())
    val isLoggedIn = _userType.asStateFlow()


    fun storeToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
        _isLoggedIn.value = true
    }

    fun getUserType() : UserType {
        val packageName = context.packageName

        return when
        {
            packageName.endsWith(".restaurant") -> UserType.RESTAURANT
            packageName.contains(".rider") -> UserType.RIDER
            else -> UserType.CUSTOMER
        }
    }
    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    private fun hasValidToken(): Boolean = getToken().isNullOrBlank().not()


    enum class UserType { RESTAURANT , CUSTOMER , RIDER }

}


