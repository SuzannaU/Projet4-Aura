package com.aura.viewModel

import android.util.Log
import com.aura.data.repository.AccountsRepository
import com.aura.data.repository.Result
import com.aura.domain.Account
import com.aura.domain.ErrorType
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
import kotlin.collections.emptyList
import kotlin.reflect.KClass

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class HomeViewModelTest {

    val repository: AccountsRepository = mockk()
    val viewModel = HomeViewModel(repository)

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @ParameterizedTest
    @MethodSource("resultProvider")
    fun getUserAccountsTest(
        result: Result<List<Account>>,
        state: KClass<out HomeViewModel.HomeUiState>,
        errorType: ErrorType?,
    ) = runTest {

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        every {
            repository.fetchUserAccounts(any())
        } returns flowOf(result)

        viewModel.getUserAccounts("userId")
        advanceUntilIdle()

        assertTrue(state.isInstance(viewModel.uiState.value))

        // for errorStates only
        if (errorType != null) {
            val errorState = viewModel.uiState.value as HomeViewModel.HomeUiState.ErrorState
            assertEquals(errorType, errorState.errorType)
        }
    }

    companion object {
        @JvmStatic
        fun resultProvider(): Stream<Arguments> {

            val accountsWithMain = listOf(Account(123, true, 2.00))
            val accountsNoMain = listOf(Account(123, false, 2.00))
            val emptyList = emptyList<Account>()

            return Stream.of(
                Arguments.of(
                    Result.Loading,
                    HomeViewModel.HomeUiState.LoadingState::class,
                    null,
                ),
                Arguments.of(
                    Result.Failure.NetworkError(""),
                    HomeViewModel.HomeUiState.ErrorState::class,
                    ErrorType.NETWORK,
                ),
                Arguments.of(
                    Result.Failure.UnreachableServer(""),
                    HomeViewModel.HomeUiState.ErrorState::class,
                    ErrorType.SERVER,
                ),
                Arguments.of(
                    Result.Failure.ServerError(""),
                    HomeViewModel.HomeUiState.ErrorState::class,
                    ErrorType.SERVER,
                ),
                Arguments.of(
                    Result.Failure.BadRequest(""),
                    HomeViewModel.HomeUiState.ErrorState::class,
                    ErrorType.BAD_REQUEST,
                ),
                Arguments.of(
                    Result.Failure.Unknown(""),
                    HomeViewModel.HomeUiState.ErrorState::class,
                    ErrorType.UNKNOWN,
                ),
                Arguments.of(
                    Result.Success(accountsNoMain),
                    HomeViewModel.HomeUiState.ErrorState::class,
                    ErrorType.NO_ACCOUNT,
                ),
                Arguments.of(
                    Result.Success(emptyList),
                    HomeViewModel.HomeUiState.ErrorState::class,
                    ErrorType.NO_ACCOUNT,
                ),
                Arguments.of(
                    Result.Success(accountsWithMain),
                    HomeViewModel.HomeUiState.BalanceFoundState::class,
                    null,
                ),
            )
        }
    }
}