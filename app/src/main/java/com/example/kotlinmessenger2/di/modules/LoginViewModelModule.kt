package com.example.kotlinmessenger2.di.modules

import androidx.lifecycle.ViewModel
import com.example.kotlinmessenger2.ui.auth.LoginViewModel
import com.example.recyclerviewmvvmtodagger.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LoginViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel
}