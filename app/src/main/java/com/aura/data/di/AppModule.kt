package com.aura.data.di

import androidx.lifecycle.ViewModelProvider
import com.aura.data.repository.AccountsRepository
import com.aura.data.repository.LoginRepository
import com.aura.data.repository.TransferRepository
import com.aura.viewModel.HomeViewModel
import com.aura.viewModel.LoginViewModel
import com.aura.viewModel.TransferViewModel
import com.aura.viewModel.viewModelFactory

interface AppModule {
    val loginRepository: LoginRepository
    val accountsRepository: AccountsRepository
    val transferRepository: TransferRepository
    val loginViewModelFactory: ViewModelProvider.Factory
    val homeViewModelFactory: ViewModelProvider.Factory
    val transferViewModelFactory: ViewModelProvider.Factory
}

class AppModuleImpl() : AppModule {

    override val loginRepository: LoginRepository
        get() = LoginRepository()

    override val accountsRepository: AccountsRepository
        get() = AccountsRepository()

    override val transferRepository: TransferRepository
        get() = TransferRepository()

    override val loginViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory {
            LoginViewModel(
                loginRepository = loginRepository,
            )
        }

    override val homeViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory {
            HomeViewModel(
                accountsRepository = accountsRepository,
            )
        }

    override val transferViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory {
            TransferViewModel(
                transferRepository = transferRepository,
            )
        }

}