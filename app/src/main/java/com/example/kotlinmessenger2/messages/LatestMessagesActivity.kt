package com.example.kotlinmessenger2.messages

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.kotlinmessenger2.R
import com.example.kotlinmessenger2.RegisterActivity
import com.example.kotlinmessenger2.models.ChatMessage
import com.example.kotlinmessenger2.models.User
import com.example.kotlinmessenger2.views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
        val TAG = "LatestMessages"
        var latestMessagesMap = HashMap<String, ChatMessage>()
    }

    val adapter = GroupAdapter<ViewHolder>()

    private lateinit var viewmodel: LatestMessageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        supportActionBar?.title = "Latest Messages"

        viewmodel = ViewModelProviders.of(this)[LatestMessageViewModel::class.java]

        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // set item click listener on your adapter
        adapter.setOnItemClickListener { item, view ->
            //Log.d(TAG, "123")
            val intent = Intent(this, ChatLogActivity::class.java)

            val row = item as LatestMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        //setupDummyRows()
        listenForLatestMessages()

        fetchCurrentUser()

        verifyUserIsLoggedIn()
    }

    private fun fetchCurrentUser() {
        viewmodel.fetchCurrentUser()
        viewmodel.currentUser.observe(this, Observer { cUser ->
            currentUser = cUser
            //Log.d(TAG, "CurrentUser Name: "+ currentUser?.username)
        })
    }

    private fun listenForLatestMessages() {

        viewmodel.listenForLatestMessages()

        viewmodel.mLatestMessagesMap.observe(this, Observer { mLatestMessagesMap ->
            latestMessagesMap = mLatestMessagesMap
            //Log.d(TAG, "latestMessagesMap: "+latestMessagesMap)
            refreshRecyclerViewMessages()
        })
    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun verifyUserIsLoggedIn() {
        val uid = viewmodel.uid()

        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
