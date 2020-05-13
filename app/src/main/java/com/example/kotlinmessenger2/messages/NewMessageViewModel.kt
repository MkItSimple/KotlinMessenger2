package com.example.kotlinmessenger2.messages

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinmessenger2.models.User
import com.example.kotlinmessenger2.repositories.UserRepository
import com.example.kotlinmessenger2.util.NODE_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageViewModel : ViewModel() {

    private val dbUsers = FirebaseDatabase.getInstance().getReference(NODE_USERS)

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() = _users

     fun fetchUsers() {

         val ref = FirebaseDatabase.getInstance().getReference("/users")
         ref.addListenerForSingleValueEvent(object: ValueEventListener {

             override fun onDataChange(snapshot: DataSnapshot) {
                 //Log.d(TAG, "DataSnapshot: " + snapshot.getValue())
//                 val adapter = GroupAdapter<ViewHolder>()
//                 val uid = FirebaseAuth.getInstance().uid
                 val musers = mutableListOf<User>()

                 snapshot.children.forEach {
                     //Log.d("NewMessage \n ", it.toString())
                     val user = it.getValue(User::class.java)
                     if (user != null) {
                         musers.add(user)
                     }
                 }
                 _users.value = musers

             }

             override fun onCancelled(p0: DatabaseError) {

             }
         })
    }
}