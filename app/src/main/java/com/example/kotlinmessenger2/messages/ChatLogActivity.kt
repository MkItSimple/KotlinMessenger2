package com.example.kotlinmessenger2.messages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinmessenger2.R
import com.example.kotlinmessenger2.models.ChatMessage
import com.example.kotlinmessenger2.models.User
import com.example.kotlinmessenger2.views.ChatFromItem
import com.example.kotlinmessenger2.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

// Convert to MVVM Dagger2
class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    var toUser: User? = null
    var fromId: String? = null
    var toId: String? = null

    val adapter = GroupAdapter<ViewHolder>()

    private lateinit var chatLogViewModel: ChatLogViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

        fromId = FirebaseAuth.getInstance().uid
        toId = toUser?.uid

        chatLogViewModel = ViewModelProviders.of(this)[ChatLogViewModel::class.java]

        listenForMessages()

        send_button_chat_log.setOnClickListener {
            //Log.d(TAG, "Attempt to send message....")
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        chatLogViewModel.listenForMessages(toUser?.uid)
        chatLogViewModel.chatMessage.observe(this, Observer { chatMessage ->
            //Log.d(TAG, "ChatMessage " + chatMessage.fromId)
            if (chatMessage.fromId == fromId) { // current loggedin user FirebaseAuth.getInstance().uid
                val currentUser = LatestMessagesActivity.currentUser
                adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
            } else {
                adapter.add(ChatToItem(chatMessage.text, toUser!!))
            }

            recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
        })
    }

    private fun performSendMessage() {

        val text = edittext_chat_log.text.toString()

        chatLogViewModel.performSendMessage(toId, fromId, text)

        chatLogViewModel.isSuccessful.observe(this, Observer { isSuccessful ->
            if(isSuccessful){
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        })
    }
}
