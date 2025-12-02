package com.aura.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.domain.ErrorType
import com.aura.data.repository.AccountsRepository
import com.aura.data.repository.Result
import com.aura.domain.Account
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(val accountsRepository: AccountsRepository) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.LoadingState)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun getUserAccounts(userId: String) {
        accountsRepository.fetchUserAccounts(userId)
            .onEach { result ->
                when (result) {
                    is Result.Loading -> onLoading(result)
                    is Result.Failure -> onFailure(result)
                    is Result.Success -> onSuccess(result)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onLoading(result: Result.Loading) {
        _uiState.value = HomeUiState.LoadingState
    }

    private fun onFailure(result: Result.Failure) {
        val errorType = when (result) {
            is Result.Failure.NetworkError -> ErrorType.NETWORK
            is Result.Failure.UnreachableServer -> ErrorType.SERVER
            is Result.Failure.ServerError -> ErrorType.SERVER
            is Result.Failure.BadRequest -> ErrorType.BAD_REQUEST
            is Result.Failure.Unknown -> ErrorType.UNKNOWN
        }
        _uiState.value = HomeUiState.ErrorState(
            errorType,
        )
    }

    private fun onSuccess(result: Result.Success<List<Account>>) {
        for (account in result.value) {
            if (account.main) {
                _uiState.value = HomeUiState.BalanceFoundState(account.balance)
                return
            }
        }
        Log.d(TAG, "getUserAccounts: No main account found")
        _uiState.value = HomeUiState.ErrorState(ErrorType.NO_ACCOUNT)
    }

    sealed class HomeUiState {
        object LoadingState : HomeUiState()

        data class BalanceFoundState(
            val balance: Double = 0.0,
        ) : HomeUiState()

        data class ErrorState(
            val errorType: ErrorType,
        ) : HomeUiState()
    }
}