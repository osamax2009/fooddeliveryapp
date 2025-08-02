package com.example.fooddeliveryapp


import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FoodHubApp : Application() {
    override fun onCreate() {
        super.onCreate()

    }
}