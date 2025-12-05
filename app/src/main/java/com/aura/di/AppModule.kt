package com.aura.di

import androidx.lifecycle.ViewModelProvider
import com.aura.data.network.AuraApiService
import com.aura.data.repository.AccountsRepository
import com.aura.data.repository.LoginRepository
import com.aura.data.repository.TransferRepository
import com.aura.viewModel.HomeViewModel
import com.aura.viewModel.LoginViewModel
import com.aura.viewModel.TransferViewModel
import com.aura.viewModel.viewModelFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface AppModule {
    val retrofit: Retrofit
    val apiService: AuraApiService
    val loginRepository: LoginRepository
    val accountsRepository: AccountsRepository
    val transferRepository: TransferRepository
    val loginViewModelFactory: ViewModelProvider.Factory
    val homeViewModelFactory: ViewModelProvider.Factory
    val transferViewModelFactory: ViewModelProvider.Factory
}

class AppModuleImpl(private val baseUrl: String) : AppModule {

    override val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .baseUrl(baseUrl)
            .build()
    }

    override val apiService: AuraApiService by lazy {
        retrofit.create(AuraApiService::class.java)
    }

    override val loginRepository: LoginRepository by lazy {
        LoginRepository(apiService)
    }

    override val accountsRepository: AccountsRepository by lazy {
        AccountsRepository(apiService)
    }

    override val transferRepository: TransferRepository by lazy {
        TransferRepository(apiService)
    }

    override val loginViewModelFactory: ViewModelProvider.Factory by lazy {
        viewModelFactory {
            LoginViewModel(
                loginRepository = loginRepository,
            )
        }
    }

    override val homeViewModelFactory: ViewModelProvider.Factory by lazy {
        viewModelFactory {
            HomeViewModel(
                accountsRepository = accountsRepository,
            )
        }
    }

    override val transferViewModelFactory: ViewModelProvider.Factory by lazy {
        viewModelFactory {
            TransferViewModel(
                transferRepository = transferRepository,
            )
        }
    }
}