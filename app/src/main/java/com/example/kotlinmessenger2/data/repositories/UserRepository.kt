package com.example.kotlinmessenger2.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class UserRepository {

    suspend fun performLogin(email: String, password: String) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                //Log.d(TAG, "Successfully logged in: ${it.result!!.user!!.uid}")
                //_authResult.value = it
                //return it.addOnCanceledListener {  }
            }
            .addOnFailureListener {
                //_authMessage.value = it
                //return@addOnFailureListener it
            }
    }
}