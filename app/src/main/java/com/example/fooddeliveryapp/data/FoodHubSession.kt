package com.example.fooddeliveryapp.data

import android.content.Context
import android.content.SharedPreferences
import com.stripe.android.customersheet.injection.CustomerSheetViewModelModule_Companion_ContextFactory.context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FoodHubSession(context: Context) {

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_id"
        private const val KEY_IS_LOGGED = "key_is_logged"
        private const val KEY_USER_TYPE = "key_user_type"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_LOGIN_TIMESTAMP = "key_login_timestamp"
    }

    val sharedPres  : SharedPreferences =
        context.getSharedPreferences("foodhub", Context.MODE_PRIVATE)

    private val _isLoggeIn = MutableStateFlow(hasValidSession())
    val isLoggedIn = _isLoggeIn.asStateFlow()

    private val _userType = MutableStateFlow(getUserType())
    val userType = _userType.asStateFlow()

    fun storeToken(token : String) {
        sharedPres.edit().putString("token",token).apply()
        updateLoginState(true)
    }
    fun storeRestaurantId(RestaurantId : String) {
        sharedPres.edit().putString("restaurantId",RestaurantId).apply()
    }

    fun getRestaurantId(): String? {
        sharedPres.getString("restaurantId",null)?.let {
            return it
        }
        return null
    }
    fun storeUserData(userId : String,
                      userName:String,
                      userType:String = "CUSTOMER",
                      userEmail: String
                      ) {
        with(sharedPres.edit()) {
            putString(KEY_USER_ID,userId)
            putString(KEY_USER_NAME,userName)
            putString(KEY_USER_TYPE,userType)
            putString(KEY_USER_EMAIL,userEmail)
            putLong(KEY_LOGIN_TIMESTAMP,System.currentTimeMillis())
            putBoolean(KEY_IS_LOGGED,true)
            apply()
        }
        _userType.value = userType
    }
    }

    fun updateLoginState(isLoggedIn : Boolean) {
        _isLoggeIn.value = isLoggedIn
       if(!isLoggedIn) {
           _userType.value = "CUSTOMER"
       }
    }








    fun getToken() : String? {
        return sharedPres.getString("token",null)?.let {
            return it
        }
        return null
    }
    fun getUserType() : String = sharedPres.getString(KEY_USER_TYPE,"CUSTOMER") ?: "CUSTOMER"


    fun hasValidSession() : Boolean {
        return !getToken().isNullOrEmpty() &&
        sharedPres.getBoolean(KEY_IS_LOGGED,false)
    }


}