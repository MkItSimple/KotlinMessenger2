package com.example.kotlinmessenger2.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinmessenger2.models.ChatMessage
import com.example.kotlinmessenger2.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LatestMessageViewModel : ViewModel() {

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    private val _chatMessage = MutableLiveData<ChatMessage>()
    val chatMessage: LiveData<ChatMessage> get() = _chatMessage

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
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                _chatMessage.value = p0.getValue(ChatMessage::class.java) ?: return
                //latestMessagesMap[p0.key!!] = chatMessage // p0.key belong to the user that we're messaging
                //refreshRecyclerViewMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                _chatMessage.value  = p0.getValue(ChatMessage::class.java) ?: return
                //latestMessagesMap[p0.key!!] = chatMessage
                //refreshRecyclerViewMessages()

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}