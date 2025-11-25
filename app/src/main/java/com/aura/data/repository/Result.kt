package com.aura.data.repository

import java.lang.Exception

sealed class Result<out T> {

    object Loading : Result<Nothing>()
    data class Success<out R>(val value: R,) : Result<R>()
    sealed class Failure() : Result<Nothing>() {
        data class NetworkError(val message: String? = "Network Error",) : Failure()
        data class BadRequest(val message: String? = "Bad Request",) : Failure()
        data class ServerError(val message: String? = "ServerError",) : Failure()
        data class Unknown(val message: String? = "Unknown Error",) : Failure()
    }

}