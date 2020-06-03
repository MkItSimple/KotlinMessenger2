package com.example.kotlinmessenger2.ui.auth

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Spy


class LoginViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Spy
    private lateinit var viewModel: LoginViewModel

    @Spy
    private lateinit var authResult: MutableLiveData<Task<AuthResult>>



    @Before
    fun setUp() {}

    @Test
    fun getAuthResult() {}

    @Test
    fun getAuthMessage() {
    }

    @Test
    fun performLogin() {
        val loginViewModel: LoginViewModel = LoginViewModel()
//        `when`<Any>(loginViewModel!!.performLogin("c1@gmail.com", "111111"))
//            .thenReturn(loginViewModel!!.authResult)
//        Log.d("Mockito", ""+loginViewModel!!.authResult)
//        val result = loginViewModel._authResult
        //`when`(loginViewModel.performLogin("c1@gmail.com", "111111")).thenReturn(loginViewModel.authResult)
        loginViewModel.performLogin("c1@gmail.com", "111111")
        Log.d("Mockito", ""+loginViewModel.authResult.value)

    }
}