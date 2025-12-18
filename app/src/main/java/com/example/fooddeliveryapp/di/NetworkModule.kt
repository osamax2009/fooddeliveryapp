package com.example.fooddeliveryapp.di

import android.content.Context
import com.example.fooddeliveryapp.data.api.AuthApiService
import com.example.fooddeliveryapp.data.api.RestaurantApiService
import com.example.fooddeliveryapp.data.api.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://170.64.225.223:8080/"

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        tokenInterceptor: TokenInterceptor
    ): OkHttpClient {
        // Logging interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Package name header interceptor
        val packageNameInterceptor = Interceptor { chain ->
            val packageName = context.packageName
            val request = chain.request().newBuilder()
                .addHeader("X-Package-Name", packageName)
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(packageNameInterceptor)
            .addInterceptor(tokenInterceptor) // Token interceptor handles auth and refresh
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRestaurantApiService(retrofit: Retrofit): RestaurantApiService {
        return retrofit.create(RestaurantApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOrderApiService(retrofit: Retrofit): com.example.fooddeliveryapp.data.api.OrderApiService {
        return retrofit.create(com.example.fooddeliveryapp.data.api.OrderApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCartApiService(retrofit: Retrofit): com.example.fooddeliveryapp.data.api.CartApiService {
        return retrofit.create(com.example.fooddeliveryapp.data.api.CartApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMenuApiService(retrofit: Retrofit): com.example.fooddeliveryapp.data.api.MenuApiService {
        return retrofit.create(com.example.fooddeliveryapp.data.api.MenuApiService::class.java)
    }

}