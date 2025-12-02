package com.aura.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransferResponse(
    @Json(name="result")
    val result: Boolean,
)
