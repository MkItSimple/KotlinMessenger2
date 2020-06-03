package com.example.kotlinmessenger2.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinmessenger2.data.repositories.UserRepository
import com.example.kotlinmessenger2.utils.EspressoIdlingResource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LoginViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userRepository: UserRepository

    private val _authResult = MutableLiveData<Task<AuthResult>>()
    val authResult: LiveData<Task<AuthResult>> get() = _authResult

    private val _authMessage = MutableLiveData<Exception>()
    val authMessage: LiveData<Exception> get() = _authMessage

    fun performLogin(email: String, password: String) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                //Log.d(TAG, "Successfully logged in: ${it.result!!.user!!.uid}")
                _authResult.value = it
            }
            .addOnFailureListener {
                _authMessage.value = it
            }
    }
}