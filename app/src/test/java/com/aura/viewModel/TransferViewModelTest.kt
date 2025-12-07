package com.aura.viewModel

import com.aura.data.repository.Result
import com.aura.data.repository.TransferRepository
import com.aura.domain.ErrorType
import com.aura.domain.Transfer
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
class TransferViewModelTest {
    
    val repository: TransferRepository = mockk()
    val viewModel = TransferViewModel(repository)

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @ParameterizedTest
    @MethodSource("resultProvider")
    fun transferTest(
        result: Result<Boolean>,
        state: KClass<out TransferViewModel.TransferUiState>,
        errorType: ErrorType?,
    ) = runTest {

        every {
            repository.transfer(any())
        } returns flowOf(result)

        viewModel.transfer(Transfer("123","123", 2.00))
        advanceUntilIdle()

        assertTrue(state.isInstance(viewModel.uiState.value))

        // for errorStates only
        if (errorType != null) {
            val errorState = viewModel.uiState.value as TransferViewModel.TransferUiState.ErrorState
            assertEquals(errorType, errorState.errorType)
        }
    }

    companion object {
        @JvmStatic
        fun resultProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Result.Loading,
                    TransferViewModel.TransferUiState.LoadingState::class,
                    null,
                ),
                Arguments.of(
                    Result.Failure.NetworkError(""),
                    TransferViewModel.TransferUiState.ErrorState::class,
                    ErrorType.NETWORK,
                ),
                Arguments.of(
                    Result.Failure.UnreachableServer(""),
                    TransferViewModel.TransferUiState.ErrorState::class,
                    ErrorType.SERVER,
                ),
                Arguments.of(
                    Result.Failure.ServerError(""),
                    TransferViewModel.TransferUiState.ErrorState::class,
                    ErrorType.BAD_RECIPIENT,
                ),
                Arguments.of(
                    Result.Failure.BadRequest(""),
                    TransferViewModel.TransferUiState.ErrorState::class,
                    ErrorType.BAD_REQUEST,
                ),
                Arguments.of(
                    Result.Failure.Unknown(""),
                    TransferViewModel.TransferUiState.ErrorState::class,
                    ErrorType.UNKNOWN,
                ),
                Arguments.of(
                    Result.Success(true),
                    TransferViewModel.TransferUiState.TransferSuccessfulState::class,
                    null,
                ),
                Arguments.of(
                    Result.Success(false),
                    TransferViewModel.TransferUiState.ErrorState::class,
                    ErrorType.TRANSFER_FAILED,
                ),
            )
        }
    }
}