package com.aura.data.response

import com.aura.domain.Account
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
) {

    fun toAccountModel(): Account {
        return Account(
            id = id,
            main = main,
            balance = balance,
        )
    }
}