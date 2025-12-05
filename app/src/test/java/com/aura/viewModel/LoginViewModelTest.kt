package com.aura.viewModel

import com.aura.data.repository.LoginRepository
import com.aura.data.repository.Result
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class LoginViewModelTest {

    val repository: LoginRepository = mockk()
    val viewModel = LoginViewModel(repository)

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun setStateTest() = runTest {

        every {
            repository.checkCredentials(any())
        } returns flow {
            emit(Result.Failure.ServerError("error"))
        }

        viewModel.login("123","123")
        println(viewModel.uiState.value.toString())
        assertTrue(viewModel.uiState.value is LoginViewModel.LoginUiState.ErrorState)

    }



}