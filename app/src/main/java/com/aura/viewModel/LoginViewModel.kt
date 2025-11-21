package com.aura.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel: ViewModel() {

    private val TAG = "LoginViewModel"

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState : StateFlow<LoginUiState> = _uiState.asStateFlow()


    fun setButton(isEnabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isButtonEnabled = isEnabled,
            )
        }
    }

    fun setLoading() {
        _uiState.update { currentState ->
            currentState.copy(
                isViewLoading = true,
            )
        }
    }

    data class LoginUiState(
        val isViewLoading: Boolean = false,
        val isButtonEnabled: Boolean = false,
        val errorMessage: String? = null,
    )
}