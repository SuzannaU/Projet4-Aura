package com.aura.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.network.ErrorType
import com.aura.data.repository.AccountsRepository
import com.aura.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(val accountsRepository: AccountsRepository) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.DefaultState)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun getUserAccounts(userId: Int) {
        accountsRepository.fetchUserAccounts(userId)
            .onEach { result ->
                when (result) {
                    Result.Loading -> _uiState.value = HomeUiState.LoadingState

                    is Result.Failure.NetworkError -> _uiState.value = HomeUiState.ErrorState(
                        result.message,
                        ErrorType.NETWORK,
                    )

                    is Result.Failure.ServerError -> _uiState.value = HomeUiState.ErrorState(
                        result.message,
                        ErrorType.SERVER,
                    )

                    is Result.Failure.BadRequest -> _uiState.value = HomeUiState.ErrorState(
                        result.message,
                        ErrorType.BAD_REQUEST,
                    )

                    is Result.Failure.Unknown -> _uiState.value = HomeUiState.ErrorState(
                        result.message,
                        ErrorType.UNKNOWN,
                    )

                    is Result.Success -> _uiState.value = HomeUiState.SuccessState(result.value)
                }
            }
            .launchIn(viewModelScope)
    }


    sealed class HomeUiState(
        val isViewLoading: Boolean,
    ) {
        object DefaultState : HomeUiState(false)
        object LoadingState : HomeUiState(true)
        data class SuccessState(
            val balance: Double = 0.0,
        ) : HomeUiState(false)

        data class ErrorState(
            val message: String? = null,
            val errorType: ErrorType,
        ) : HomeUiState(false)
    }
}