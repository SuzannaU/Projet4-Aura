package com.aura.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountResponse(
    @Json(name = "id")
    val id: Int,

    @Json(name = "main")
    val main: Boolean,

    @Json(name = "balance")
    val balance: Double
)