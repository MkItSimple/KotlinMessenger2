package com.example.kotlinmessenger2

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.kotlinmessenger2.RegisterActivity.Companion.CHANNEL_ID
import com.example.kotlinmessenger2.messages.LatestMessagesActivity

// Notification Builder . . responsible building and sending the notification
class NotificationHelper {

    fun displayNotification(
        context: Context,
        title: String,
        body: String
    ) {
        val intent = Intent(context, LatestMessagesActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            100,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT // will delete or cancel current notification before creating new one
        )
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("My notification")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

            // Set the intent that will fire when the user taps the notification
            //.setContentIntent(pendingIntent) // #8 when notification clicked
            .setAutoCancel(true)

        val mNotificationMgr = NotificationManagerCompat.from(context)
        mNotificationMgr.notify(1, mBuilder.build()) // to display notification
    }
}