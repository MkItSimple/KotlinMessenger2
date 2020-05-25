package com.example.kotlinmessenger2.messages

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger2.Api
import com.example.kotlinmessenger2.R
import com.example.kotlinmessenger2.models.ChatMessage
import com.example.kotlinmessenger2.models.User
import com.example.kotlinmessenger2.util.toast
import com.example.kotlinmessenger2.views.ChatFromItem
import com.example.kotlinmessenger2.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

// Convert to MVVM Dagger2
class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var toUser: User? = null
    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        token = toUser?.token.toString()
        val currentUID = mAuth.uid
        //Toast.makeText(this, "From: $currentUID", Toast.LENGTH_LONG).show()

        supportActionBar?.title = toUser?.username

        listenForMessages()
        //setDummyData()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            performSendMessage(token!!)
        }
    }

    private fun listenForMessages() {
        val fromId = mAuth.uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == mAuth.uid) {
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatToItem(chatMessage.text, currentUser))
                    } else {
                        adapter.add(ChatFromItem(chatMessage.text, toUser!!))
                    }
                }

                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }

    private fun performSendMessage(token: String) {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()

        val fromId = mAuth.uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user!!.uid

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)

        // send notification
        val retrofit = Retrofit.Builder()
            .baseUrl("https://kotlinmessenger-3bcd8.web.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api =
            retrofit.create(
                Api::class.java
            )
        
        val call = api.sendNotification(token, "Choreyn Anania", text)

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                try {
                    toast(response.body()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                t: Throwable
            ) {
            }
        })
    }
}
