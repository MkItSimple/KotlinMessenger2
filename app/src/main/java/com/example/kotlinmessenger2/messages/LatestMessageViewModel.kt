package com.example.kotlinmessenger2.messages

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinmessenger2.RegisterActivity
import com.example.kotlinmessenger2.models.ChatMessage
import com.example.kotlinmessenger2.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.withContext

class LatestMessageViewModel : ViewModel() {

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    private val _chatMessage = MutableLiveData<ChatMessage>()
    val chatMessage: LiveData<ChatMessage> get() = _chatMessage

    private val _mLatestMessagesMap = MutableLiveData<HashMap<String, ChatMessage>>()
    val mLatestMessagesMap: LiveData<HashMap<String, ChatMessage>> get() = _mLatestMessagesMap



     fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                _currentUser.value = p0.getValue(User::class.java)
                //Log.d("LatestMessages", "Current user ${cUser.profileImageUrl}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        val latestMessagesMap = HashMap<String, ChatMessage>()

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage // p0.key belong to the user that we're messaging
                _mLatestMessagesMap.value = latestMessagesMap
            }

            override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                _mLatestMessagesMap.value = latestMessagesMap
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun uid() : String? {
        return FirebaseAuth.getInstance().uid
    }
}