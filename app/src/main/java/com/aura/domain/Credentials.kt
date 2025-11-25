package com.aura.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Credentials(
    @Json(name = "id")
    val id: String,

    @Json(name = "password")
    val password: String,
)
