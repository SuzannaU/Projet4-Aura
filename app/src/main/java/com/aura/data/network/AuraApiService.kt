package com.aura.data.network

import com.aura.data.response.AccountResponse
import com.aura.data.response.LoginResponse
import com.aura.data.response.TransferResponse
import com.aura.domain.Credentials
import com.aura.domain.Transfer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuraApiService {
    @POST("login")
    suspend fun login(@Body credentials: Credentials): Response<LoginResponse>

    @GET("accounts/{id}")
    suspend fun fetchUserAccounts(@Path("id") userId: String): Response<List<AccountResponse>>

    @POST("transfer")
    suspend fun transfer(@Body transfer: Transfer): Response<TransferResponse>
}
