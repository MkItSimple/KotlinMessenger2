package com.example.kotlinmessenger2

import android.util.Log
import com.example.kotlinmessenger2.RegisterActivity.Companion.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// This will recieve the incoming messages of notifications
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var helper: NotificationHelper? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG," onMessageReceived ")

//        if(remoteMessage.data.isNotEmpty()){
//            Log.d(TAG, " Data : " + remoteMessage.data.toString())
//        }

        // if remote message not null
        if(remoteMessage.notification != null){
            Log.d(TAG," Notification : " + remoteMessage.notification!!.body.toString())
            val title: String = remoteMessage.notification?.title!!
            val body: String = remoteMessage.notification?.body!!

            Log.d(TAG, " choreyn : $title $body")
            // helper will build and display the notification recieved by this MyFirebaseMessagingService
            helper?.displayNotification(applicationContext, title, body)
        }

    }



}