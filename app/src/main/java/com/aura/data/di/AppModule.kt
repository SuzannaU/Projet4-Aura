package com.aura.data.di

import androidx.lifecycle.ViewModelProvider
import com.aura.data.repository.CredentialRepository
import com.aura.viewModel.LoginViewModel
import com.aura.viewModel.viewModelFactory

interface AppModule {
    val repository: CredentialRepository
    val loginViewModelFactory: ViewModelProvider.Factory
}

class AppModuleImpl() : AppModule {

    override val repository: CredentialRepository
        get() = CredentialRepository()

    override val loginViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory {
            LoginViewModel(
                credentialsRepository = repository,
            )
        }

}