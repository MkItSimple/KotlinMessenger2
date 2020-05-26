package com.example.kotlinmessenger2.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinmessenger2.models.User
import com.example.kotlinmessenger2.util.NODE_USERS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersViewModel : ViewModel() {

    private val dbUsers = FirebaseDatabase.getInstance().getReference(NODE_USERS)

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun fetchFilteredUsers(uid: String) {
        val dbUsers = dbUsers.child(uid)

        dbUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    _user.value = snapshot.getValue(User::class.java)
                }
            }
        })
    }

}