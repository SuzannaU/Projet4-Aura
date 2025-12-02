package com.aura.data.repository

import android.util.Log
import com.aura.data.network.AuraApi
import com.aura.domain.Transfer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.ConnectException
import java.net.SocketTimeoutException

class TransferRepository() {
    private val TAG = "TransferRepository"

    fun transfer(transfer: Transfer): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        delay(1000)
        try {
            val response = AuraApi.retrofitService.transfer(transfer)
            val isSuccessful = response.body()?.result ?: false
            val responseCode = response.code()

            when (responseCode) {
                200 -> emit(Result.Success(isSuccessful))
                400 -> emit(Result.Failure.BadRequest())
                in 500..599 -> emit(Result.Failure.ServerError())
                else -> emit(Result.Failure.Unknown())
            }

        } catch (e: Exception) {
            Log.e(TAG, "fetchUserAccounts: ${e.message}")
            val failure = when (e) {
                is SocketTimeoutException -> Result.Failure.ServerError("Connection Timeout")
                is ConnectException -> Result.Failure.NetworkError("No connection")
                else -> Result.Failure.Unknown()
            }
            emit(failure)
        }
    }
}