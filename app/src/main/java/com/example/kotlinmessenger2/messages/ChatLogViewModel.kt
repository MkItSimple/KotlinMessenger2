package com.example.kotlinmessenger2.messages

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinmessenger2.models.ChatMessage
import com.example.kotlinmessenger2.util.NODE_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogViewModel : ViewModel() {

    private val dbUsers = FirebaseDatabase.getInstance().getReference(NODE_USERS)

    private val _chatMessage = MutableLiveData<ChatMessage>()
    val chatMessage: LiveData<ChatMessage>
        get() = _chatMessage

    private val _isSuccessful = MutableLiveData<Boolean>()
    val isSuccessful: LiveData<Boolean>
        get() = _isSuccessful


    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {}

        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {

        }

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val mChatMessage = snapshot.getValue(ChatMessage::class.java)
            _chatMessage.value = mChatMessage
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }
    }

    fun getRealtimeUpdates() {
        dbUsers.addChildEventListener(childEventListener)
    }

    fun listenForMessages(uid: String?) {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(ChatLogActivity.TAG, "onCancelled")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.d(ChatLogActivity.TAG, "onChildMoved")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.d(ChatLogActivity.TAG, "onChildChanged")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    _chatMessage.value = chatMessage
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                Log.d(ChatLogActivity.TAG, "onChildRemoved")
            }

        })
    }

    fun performSendMessage(toId: String?, fromId: String?, text: String) {
        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId!!, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(ChatLogActivity.TAG, "Saved our chat message: ${reference.key}")
                //edittext_chat_log.text.clear()
                //recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                _isSuccessful.value = true

            }
            .addOnFailureListener{
                _isSuccessful.value = false
            }

        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }

}