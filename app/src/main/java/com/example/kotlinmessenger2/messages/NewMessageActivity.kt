package com.example.kotlinmessenger2.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinmessenger2.R
import com.example.kotlinmessenger2.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    companion object {
        val TAG = "NewMessageActivity"
        val USER_KEY = "USER_KEY"
    }

    private lateinit var newMessageViewModel: NewMessageViewModel

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User . . New Messages"

        newMessageViewModel = ViewModelProviders.of(this)[NewMessageViewModel::class.java]

        //fetchUsers()
        newMessageViewModel.fetchUsers()

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        newMessageViewModel.users.observe(this, Observer { users ->

            for (user in users){
                Log.d(TAG, "User: "+ user.username)

                val uid = FirebaseAuth.getInstance().uid
                if (user.uid != uid) {
                    adapter.add(
                        UserItem(user)
                    )
                }
            }

            adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()
                }

            recyclerview_newmessage.adapter = adapter
        })
    }
}
