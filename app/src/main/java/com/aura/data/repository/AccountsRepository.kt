package com.aura.data.repository

import android.util.Log
import com.aura.data.network.AuraApi
import com.aura.domain.Account
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.ConnectException
import java.net.SocketTimeoutException

class AccountsRepository() {

    private val TAG = "AccountsRepository"

    fun fetchUserAccounts(userId: String): Flow<Result<List<Account>>> = flow {
        emit(Result.Loading)
        delay(1000)
        try {
            val response = AuraApi.retrofitService.fetchUserAccounts(userId)
            val responseCode = response.code()
            val responseAccounts = response.body()

            when (responseCode) {
                200 ->
                    if (responseAccounts == null || responseAccounts.isEmpty()) {
                        emit(Result.Success(emptyList()))
                    } else {
                        val modelList = mutableListOf<Account>()
                        responseAccounts.forEach { accountResponse ->
                            modelList.add(accountResponse.toAccountModel())
                        }
                        emit(Result.Success(modelList))
                    }

                400 -> emit(Result.Failure.BadRequest())
                in 500..599 -> emit(Result.Failure.ServerError())
                else -> emit(Result.Failure.Unknown())
            }

        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "fetchUserAccounts: ${e.message}")
            emit(Result.Failure.ServerError("Connection timeout"))
        } catch (e: ConnectException) {
            Log.e(TAG, "fetchUserAccounts: ${e.message}")
            emit(Result.Failure.NetworkError("No connection"))
        } catch (e: Exception) {
            Log.e(TAG, "fetchUserAccounts: error with exception: $e")
            emit(Result.Failure.Unknown())
        }
    }
}