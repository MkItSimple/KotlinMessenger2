package com.example.kotlinmessenger2

import android.app.Application
import com.example.kotlinmessenger2.di.AppComponent
import com.example.kotlinmessenger2.di.DaggerAppComponent

@Suppress("DEPRECATION")
class BaseApplication : Application(){

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        this.appComponent = this.initDagger()
    }

    private fun initDagger()  = DaggerAppComponent.builder()
        //.appModule(AppModule())
        .build()
}