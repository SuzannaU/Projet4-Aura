package com.aura.data.repository

sealed class Result<out T> {

    object Loading : Result<Nothing>()
    data class Success<out R>(val value: R,) : Result<R>()
    sealed class Failure(val errorMessage: String?) : Result<Nothing>() {
        data class NetworkError(val message: String? = "Network Error",) : Failure(message)
        data class UnreachableServer(val message: String? = "Server is not reachable") : Failure(message)
        data class BadRequest(val message: String? = "Bad Request",) : Failure(message)
        data class ServerError(val message: String? = "ServerError",) : Failure(message)
        data class Unknown(val message: String? = "Unknown Error",) : Failure(message)
    }
}