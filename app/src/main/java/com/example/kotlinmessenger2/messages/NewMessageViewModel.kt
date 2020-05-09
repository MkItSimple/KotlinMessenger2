package com.example.kotlinmessenger2.messages

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinmessenger2.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageViewModel : ViewModel() {

    //private val pOMutableLiveData : List<Any> = MutableLiveData<listOf<Users>>()
    val p0LiveData: LiveData<List<User>>? = null

     fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
//                Log.d(TAG, "DataSnapshot: " + p0.value)
//                val adapter = GroupAdapter<ViewHolder>()
//                val uid = FirebaseAuth.getInstance().uid

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
//                    val user = it.getValue(User::class.java)
//                    if (user != null && user.uid != uid) {
//                        adapter.add(
//                            UserItem(
//                                user
//                            )
//                        )
//                    }
                }
//
//                adapter.setOnItemClickListener { item, view ->
//
//                    val userItem = item as UserItem
//
//                    val intent = Intent(view.context, ChatLogActivity::class.java)
//                    intent.putExtra(USER_KEY, userItem.user)
//                    startActivity(intent)
//
//                    finish()
//                }
//
//                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}