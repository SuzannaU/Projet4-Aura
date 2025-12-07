package com.aura.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.domain.ErrorType
import com.aura.data.repository.LoginRepository
import com.aura.data.repository.Result
import com.aura.domain.Credentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginViewModel(val loginRepository: LoginRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.DefaultState)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(id: String, password: String) {
        val credentials = Credentials(id, password)
        loginRepository.checkCredentials(credentials)
            .onEach { result ->
                when (result) {
                    is Result.Loading -> onLoading()
                    is Result.Failure -> onFailure(result)
                    is Result.Success -> onSuccess(result)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onLoading() {
        _uiState.value = LoginUiState.LoadingState
    }

    private fun onFailure(result: Result.Failure) {
        val errorType = when (result) {
            is Result.Failure.NetworkError -> ErrorType.NETWORK
            is Result.Failure.UnreachableServer -> ErrorType.SERVER
            is Result.Failure.ServerError -> ErrorType.SERVER
            is Result.Failure.BadRequest -> ErrorType.BAD_REQUEST
            is Result.Failure.Unknown -> ErrorType.UNKNOWN
        }
        _uiState.value = LoginUiState.ErrorState(
            errorType,
        )
    }

    private fun onSuccess(result: Result.Success<Boolean>) {
        if (result.value) {
            _uiState.value = LoginUiState.GrantedState
        } else {
            _uiState.value = LoginUiState.ErrorState(ErrorType.BAD_CREDENTIALS)
        }
    }

    sealed class LoginUiState {
        object DefaultState : LoginUiState()
        object LoadingState : LoginUiState()
        object GrantedState : LoginUiState()

        data class ErrorState(
            val errorType: ErrorType,
        ) : LoginUiState()
    }
}