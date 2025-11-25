package com.aura.data.di

import androidx.lifecycle.ViewModelProvider
import com.aura.data.repository.AccountsRepository
import com.aura.data.repository.CredentialsRepository
import com.aura.viewModel.HomeViewModel
import com.aura.viewModel.LoginViewModel
import com.aura.viewModel.viewModelFactory

interface AppModule {
    val credentialsRepository: CredentialsRepository
    val accountsRepository: AccountsRepository
    val loginViewModelFactory: ViewModelProvider.Factory
    val homeViewModelFactory: ViewModelProvider.Factory
}

class AppModuleImpl() : AppModule {

    override val credentialsRepository: CredentialsRepository
        get() = CredentialsRepository()

    override val accountsRepository: AccountsRepository
        get() = AccountsRepository()

    override val loginViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory {
            LoginViewModel(
                credentialsRepository = credentialsRepository,
            )
        }

    override val homeViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory {
            HomeViewModel(
                accountsRepository = accountsRepository,
            )
        }

}