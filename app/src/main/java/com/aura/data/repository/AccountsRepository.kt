package com.aura.data.repository

import android.util.Log
import com.aura.data.network.AuraApiService
import com.aura.data.response.AccountResponse
import com.aura.domain.Account
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.ConnectException
import java.net.SocketTimeoutException

class AccountsRepository(val apiService: AuraApiService) {
    private val TAG = "AccountsRepository"

    fun fetchUserAccounts(userId: String): Flow<Result<List<Account>>> = flow {
        emit(Result.Loading)
        delay(1000)
        try {
            val response = apiService.fetchUserAccounts(userId)
            val responseCode = response.code()
            val responseAccounts = response.body() ?: emptyList()

            when (responseCode) {
                200 -> emit(Result.Success(responseAccounts.map(AccountResponse::toAccountModel)))
                400 -> emit(Result.Failure.BadRequest())
                in 500..599 -> emit(Result.Failure.ServerError())
                else -> emit(Result.Failure.Unknown())
            }

        } catch (e: Exception) {
            Log.e(TAG, "fetchUserAccounts: ${e.message}")
            val failure = when (e) {
                is SocketTimeoutException -> Result.Failure.UnreachableServer("Connection Timeout")
                is ConnectException -> Result.Failure.NetworkError("No connection")
                else -> Result.Failure.Unknown()
            }
            emit(failure)
        }
    }
}