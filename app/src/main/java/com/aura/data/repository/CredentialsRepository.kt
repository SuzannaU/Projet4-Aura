package com.aura.data.repository

import android.system.ErrnoException
import android.util.Log
import com.aura.data.network.AuraApi
import com.aura.domain.Credentials
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.ConnectException
import java.net.SocketTimeoutException

class CredentialRepository() {
    private val TAG = "CredentialsRepository"

    fun checkCredentials(credentials: Credentials): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        delay(1000)
        try {
            val response = AuraApi.retrofitService.login(credentials)
            val isGranted = response.body()?.granted ?: false
            val responseCode = response.code()
            when (responseCode) {
                200 -> emit(Result.Success(isGranted))
                400 -> emit(Result.Failure.BadRequest())
                in 500..599 -> emit(Result.Failure.ServerError())
                else -> emit(Result.Failure.Unknown())
            }
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "checkCredentials: ${e.message}", )
            emit(Result.Failure.ServerError("Connection timeout"))
        } catch (e: ConnectException) {
            Log.e(TAG, "checkCredentials: ${e.message}", )
            emit(Result.Failure.NetworkError("No connection"))
        } catch (e: Exception) {
            Log.e(TAG, "checkCredentials: error with exception: $e", )
            emit(Result.Failure.Unknown())
        }
    }
}