package com.aura.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.domain.ErrorType
import com.aura.data.repository.Result
import com.aura.data.repository.TransferRepository
import com.aura.domain.Transfer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TransferViewModel(val transferRepository: TransferRepository) : ViewModel() {
    private val TAG = "TransferViewModel"

    private val _uiState = MutableStateFlow<TransferUiState>(TransferUiState.DefaultState)
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    fun transfer(transfer: Transfer) {
        transferRepository.transfer(transfer)
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
        _uiState.value = TransferUiState.LoadingState
    }

    private fun onFailure(result: Result.Failure) {
        val errorType = when (result) {
            is Result.Failure.NetworkError -> ErrorType.NETWORK
            is Result.Failure.ServerError -> ErrorType.SERVER
            is Result.Failure.BadRequest -> ErrorType.BAD_REQUEST
            is Result.Failure.Unknown -> ErrorType.UNKNOWN
        }
        _uiState.value = TransferUiState.ErrorState(
            errorType = errorType,
        )
    }

    private fun onSuccess(result: Result.Success<Boolean>) {
        if (result.value) {
            _uiState.value = TransferUiState.TransferSuccessfulState
        } else {
            _uiState.value = TransferUiState.ErrorState(errorType = ErrorType.TRANSFER_FAILED)
        }
    }

    sealed class TransferUiState {
        object DefaultState : TransferUiState()
        object LoadingState : TransferUiState()
        object TransferSuccessfulState : TransferUiState()

        data class ErrorState(
            val errorType: ErrorType,
        ) : TransferUiState()

    }
}