package com.aura.repository

import android.util.Log
import com.aura.data.network.AuraApiService
import com.aura.data.repository.LoginRepository
import com.aura.data.repository.Result
import com.aura.data.response.LoginResponse
import com.aura.domain.Credentials
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.stream.Stream
import kotlin.reflect.KClass

class LoginRepositoryTest {

    val apiService: AuraApiService = mockk()
    val repository: LoginRepository = LoginRepository(apiService)
    val testCredentials = Credentials("userId", "password")

    @ParameterizedTest(name = "Response {0} should emit {1} times, with Result types {2}, {3}")
    @MethodSource("responseProvider")
    fun checkCredentialsTest_withNoException(
        response: Response<LoginResponse>,
        size: Int,
        result0: KClass<out Result<*>>,
        result1: KClass<out Result<*>>,
    ) = runTest {

        coEvery { apiService.login(any()) } returns response

        val results = mutableListOf<Result<Boolean>>()
        repository.checkCredentials(testCredentials).collect { result -> results.add(result) }

        assertEquals(size, results.size)
        assertTrue(result0.isInstance(results[0]))
        assertTrue(result1.isInstance(results[1]))
    }

    @ParameterizedTest(name = "Exception of class {0} should emit {1} times, with Result types {2}, {3}")
    @MethodSource("exceptionProvider")
    fun checkCredentials_withException(
        exception: Exception,
        size: Int,
        result0: KClass<out Result<*>>,
        result1: KClass<out Result<*>>,
    ) = runTest {

        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0

        coEvery { apiService.login(any()) } throws exception

        val results = mutableListOf<Result<Boolean>>()
        repository.checkCredentials(testCredentials).collect { result -> results.add(result) }

        assertEquals(size, results.size)
        assertTrue(result0.isInstance(results[0]))
        assertTrue(result1.isInstance(results[1]))
    }

    companion object {

        @JvmStatic
        fun responseProvider(): Stream<Arguments> {

            val body = ResponseBody.create(
                MediaType.parse("application/json"),
                """{"granted":false}""",
            )

            return Stream.of(
                Arguments.of(
                    Response.success(200, LoginResponse(true)),
                    2,
                    Result.Loading::class,
                    Result.Success::class,
                ),
                Arguments.of(
                    Response.error<LoginResponse>(400, body),
                    2,
                    Result.Loading::class,
                    Result.Failure.BadRequest::class,
                ),
                Arguments.of(
                    Response.error<LoginResponse>(500, body),
                    2,
                    Result.Loading::class,
                    Result.Failure.ServerError::class,
                ),
                Arguments.of(
                    Response.error<LoginResponse>(600, body),
                    2,
                    Result.Loading::class,
                    Result.Failure.Unknown::class,
                ),
            )
        }

        @JvmStatic
        fun exceptionProvider(): Stream<Arguments> {

            return Stream.of(
                Arguments.of(
                    SocketTimeoutException(),
                    2,
                    Result.Loading::class,
                    Result.Failure.UnreachableServer::class,
                ),
                Arguments.of(
                    ConnectException(),
                    2,
                    Result.Loading::class,
                    Result.Failure.NetworkError::class,
                ),
                Arguments.of(
                    Exception(),
                    2,
                    Result.Loading::class,
                    Result.Failure.Unknown::class,
                ),
            )
        }
    }

}