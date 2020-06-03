package com.example.kotlinmessenger2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinmessenger2.R
import com.example.kotlinmessenger2.ui.messages.LatestMessagesActivity
import com.example.kotlinmessenger2.utils.toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {
        val TAG = "LoginActivity"
    }

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProviders.of(this)[LoginViewModel::class.java]

        login_button_login.setOnClickListener {
            performLogin()
        }

        back_to_register_textview.setOnClickListener{
            finish()
        }
    }

    private fun performLogin() {
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            toast("Please fill out email/pw.")
            return
        }

        loginViewModel.performLogin(email, password)

        loginViewModel.authResult.observe(this, Observer {
            //Log.d(TAG, "Successfully logged in: ${it.user}")
            if (it.isSuccessful) {
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        })

        loginViewModel.authMessage.observe(this, Observer {
            toast("Failed to log in: ${it.message}")
        })
    }
}
