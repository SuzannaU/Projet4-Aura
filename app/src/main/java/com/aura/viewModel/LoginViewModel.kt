package com.aura.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.network.ErrorType
import com.aura.data.repository.LoginRepository
import com.aura.data.repository.Result
import com.aura.domain.Credentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginViewModel(val loginRepository: LoginRepository) : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.DefaultState)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(id: String, password: String) {
        val credentials = Credentials(id, password)
        loginRepository.checkCredentials(credentials)
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
        _uiState.value = LoginUiState.LoadingState
    }

    private fun onFailure(result: Result.Failure) {
        val eType = when (result) {
            is Result.Failure.NetworkError -> ErrorType.NETWORK
            is Result.Failure.ServerError -> ErrorType.SERVER
            is Result.Failure.BadRequest -> ErrorType.BAD_REQUEST
            is Result.Failure.Unknown -> ErrorType.UNKNOWN
        }
        _uiState.value = LoginUiState.ErrorState(
            result.errorMessage,
            eType,
        )
    }

    private fun onSuccess(result: Result.Success<Boolean>) {
        if (result.value) {
            _uiState.value = LoginUiState.GrantedState
        } else {
            _uiState.value = LoginUiState.NotGrantedState(ErrorType.BAD_CREDENTIALS)
        }
    }

    sealed class LoginUiState(
        val isViewLoading: Boolean,
    ) {
        object DefaultState : LoginUiState(false)
        object LoadingState : LoginUiState(true)
        object GrantedState : LoginUiState(false)

        data class NotGrantedState(
            val errorType: ErrorType,
        ) : LoginUiState(false,)

        data class ErrorState(
            val message: String? = null,
            val errorType: ErrorType,
        ) : LoginUiState(false)
    }
}