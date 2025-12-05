package com.aura.viewModel

import com.aura.data.repository.AccountsRepository
import com.aura.data.repository.Result
import com.aura.domain.ErrorType
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class HomeViewModelTest {

    val repository: AccountsRepository = mockk()
    val viewModel = HomeViewModel(repository)

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun setStateTest() = runTest {

        //viewModel.getUserAccounts("1")

        every {
            repository.fetchUserAccounts(any())
        } returns flow {
            emit(Result.Failure.ServerError("error"))
        }

        viewModel.getUserAccounts("12")
        println(viewModel.uiState.value.toString())
        assertTrue(viewModel.uiState.value is HomeViewModel.HomeUiState.ErrorState)

    }

}