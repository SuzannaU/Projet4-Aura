package com.aura.data.network

object AuraApi {
    val retrofitService : AuraApiService by lazy {
        retrofit.create(AuraApiService::class.java)
    }
}