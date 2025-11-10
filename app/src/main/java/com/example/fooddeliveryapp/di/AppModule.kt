package com.example.fooddeliveryapp.di

import android.content.Context
import com.example.fooddeliveryapp.data.SessionManager
import com.example.fooddeliveryapp.data.api.RestaurantApiService
import com.example.fooddeliveryapp.data.repository.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideDataRepository(
        restaurantApiService: RestaurantApiService,
        sessionManager: SessionManager
    ): DataRepository {
        return DataRepository(restaurantApiService, sessionManager)
    }
}