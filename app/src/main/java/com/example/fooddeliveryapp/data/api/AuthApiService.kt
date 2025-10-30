package com.example.fooddeliveryapp.data.api

import com.example.fooddeliveryapp.data.model.AuthResponse
import com.example.fooddeliveryapp.data.model.LoginRequest
import com.example.fooddeliveryapp.data.model.OAuthRequest
import com.example.fooddeliveryapp.data.model.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun signup(@Body signupRequest: SignupRequest): Response<AuthResponse>

    @POST("auth/oauth")
    suspend fun oauthLogin(@Body oAuthRequest: OAuthRequest): Response<AuthResponse>
}