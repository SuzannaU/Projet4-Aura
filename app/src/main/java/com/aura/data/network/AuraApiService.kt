package com.aura.data.network

import com.aura.data.response.CredentialsResponse
import com.aura.domain.Credentials
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private const val BASE_URL = "http://10.0.2.2:8080"

val retrofit = Retrofit.Builder()
    .addConverterFactory(
        MoshiConverterFactory.create(
            Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        )
    )
    .baseUrl(BASE_URL)
    .build()

interface AuraApiService {
    @POST("login")
    suspend fun login(@Body credentials: Credentials): Response<CredentialsResponse>
}
