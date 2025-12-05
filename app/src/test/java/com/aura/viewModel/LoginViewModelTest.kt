package com.aura.viewModel

import com.aura.data.repository.LoginRepository
import com.aura.data.repository.Result
import com.aura.domain.ErrorType
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.reflect.KClass

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class LoginViewModelTest {

    val repository: LoginRepository = mockk()
    val viewModel = LoginViewModel(repository)

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @ParameterizedTest
    @MethodSource("resultProvider")
    fun setStateTest_noError(
        result: Result<Boolean>,
        state: KClass<out LoginViewModel.LoginUiState>,
        errorType: ErrorType?,
    ) = runTest {

        every {
            repository.checkCredentials(any())
        } returns flowOf(result)

        viewModel.login("123","123")
        advanceUntilIdle()

        assertTrue(state.isInstance(viewModel.uiState.value))

        // for errorStates only
        if (errorType != null) {
            val errorState = viewModel.uiState.value as LoginViewModel.LoginUiState.ErrorState
            assertEquals(errorType, errorState.errorType)
        }
    }

    companion object {
        @JvmStatic
        fun resultProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Result.Loading,
                    LoginViewModel.LoginUiState.LoadingState::class,
                    null,
                ),
                Arguments.of(
                    Result.Failure.NetworkError(""),
                    LoginViewModel.LoginUiState.ErrorState::class,
                    ErrorType.NETWORK,
                ),
                Arguments.of(
                    Result.Failure.UnreachableServer(""),
                    LoginViewModel.LoginUiState.ErrorState::class,
                    ErrorType.SERVER,
                ),
                Arguments.of(
                    Result.Failure.ServerError(""),
                    LoginViewModel.LoginUiState.ErrorState::class,
                    ErrorType.SERVER,
                ),
                Arguments.of(
                    Result.Failure.BadRequest(""),
                    LoginViewModel.LoginUiState.ErrorState::class,
                    ErrorType.BAD_REQUEST,
                ),
                Arguments.of(
                    Result.Failure.Unknown(""),
                    LoginViewModel.LoginUiState.ErrorState::class,
                    ErrorType.UNKNOWN,
                ),
                Arguments.of(
                    Result.Success(true),
                    LoginViewModel.LoginUiState.GrantedState::class,
                    null,
                ),
                Arguments.of(
                    Result.Success(false),
                    LoginViewModel.LoginUiState.ErrorState::class,
                    ErrorType.BAD_CREDENTIALS,
                ),
            )
        }
    }
}