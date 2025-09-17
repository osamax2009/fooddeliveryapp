package com.example.fooddeliveryapp.data.api

import com.example.fooddeliveryapp.data.model.AuthResponse
import com.example.fooddeliveryapp.data.model.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>
}