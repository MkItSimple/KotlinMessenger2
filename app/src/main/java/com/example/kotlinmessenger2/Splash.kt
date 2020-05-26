package com.example.kotlinmessenger2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
            }
        }, 500)
    }
}

/*
*
* 815df6
*1773eb blue
* */
