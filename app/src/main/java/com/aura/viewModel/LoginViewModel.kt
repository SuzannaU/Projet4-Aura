package com.aura.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.network.ErrorType
import com.aura.data.repository.CredentialsRepository
import com.aura.data.repository.Result
import com.aura.domain.Credentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginViewModel(val credentialsRepository: CredentialsRepository) : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.DefaultState)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(id: String, password: String) {
        val credentials = Credentials(id, password)
        credentialsRepository.checkCredentials(credentials)
            .onEach { result ->
                when (result) {

                    Result.Loading -> _uiState.value = LoginUiState.LoadingState

                    is Result.Failure.NetworkError -> _uiState.value = LoginUiState.ErrorState(
                        result.message,
                        ErrorType.NETWORK,
                    )
                    is Result.Failure.ServerError -> _uiState.value = LoginUiState.ErrorState(
                        result.message,
                        ErrorType.SERVER,
                    )
                    is Result.Failure.BadRequest -> _uiState.value = LoginUiState.ErrorState(
                        result.message,
                        ErrorType.BAD_REQUEST,
                    )
                    is Result.Failure.Unknown -> _uiState.value = LoginUiState.ErrorState(
                        result.message,
                        ErrorType.UNKNOWN,
                    )

                    is Result.Success -> {
                        if (result.value) {
                            _uiState.value = LoginUiState.SuccessState
                        } else {
                            _uiState.value = LoginUiState.ErrorState(
                                "bad credentials",
                                ErrorType.BAD_CREDENTIALS,
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    sealed class LoginUiState(
        val isViewLoading: Boolean,
    ) {
        object DefaultState : LoginUiState(false,)
        object LoadingState : LoginUiState(true,)
        object SuccessState : LoginUiState(false,)
        data class ErrorState(
            val message: String? = null,
            val errorType: ErrorType,
        ) : LoginUiState(false)
    }
}