package com.example.kotlinmessenger2.messages

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinmessenger2.R
import com.example.kotlinmessenger2.models.*
import com.example.kotlinmessenger2.util.toast
import com.example.kotlinmessenger2.views.ChatFromItem
import com.example.kotlinmessenger2.views.ChatToItem
import com.example.kotlinmessenger2.views.ImageFromItem
import com.example.kotlinmessenger2.views.ImageToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import java.util.*

// Convert to MVVM Dagger2
class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var toUser: User? = null
    var token: String? = null
    var currentUser: User? = null

    private lateinit var viewModel: UsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        viewModel = ViewModelProviders.of(this).get(UsersViewModel::class.java)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        token = toUser?.token.toString()
        val uid = mAuth.uid
        //Toast.makeText(this, "From: $currentUID", Toast.LENGTH_LONG).show()

        supportActionBar?.title = toUser?.username

        getCurrentUser(uid!!)

        listenForMessages()
        //setDummyData()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send text message....")
            performSendMessage(token!!)
        }

        send_image.setOnClickListener {
            Log.d(TAG, "Attempt to send image message....")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null // we put this outide the function . . so that we can use it later on

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data // is the uri . . basically where that image stored in the device
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)  // before we can use selectedImagePath we need to  make it as bitmap

            uploadImageToFirebaseStorage()
        }
    }

    // upload image to firebase storage
    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/messages/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")
                    //saveUserToFirebaseDatabase(it.toString(), token)
                    performSendImageMessage(it.toString(), token!!)
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun performSendImageMessage(fileLocation: String, token: String) {
        val fromId = mAuth.uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user!!.uid

        if (fromId == null) return

        // chat copy for sender
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        // chat copy for reciever
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val imageMessage = ImageMessage(reference.key!!, fileLocation, fromId, toId, System.currentTimeMillis() / 1000)

        //Log.d("ImageMessage", "Image Path: " + imageMessage.imagePath + "Image Type:" + imageMessage.type)

        // setValue inerted the chat to database . . . then scroll recyclerview to the bottom
        reference.setValue(imageMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(imageMessage)
    }



    private fun getCurrentUser(uid: String) {
        viewModel.fetchFilteredUsers(uid)

        viewModel.user.observe(this, Observer {
            currentUser = it
        })
    }

    private fun fetchFilteredUsers(uid: String){
        val dbUsers = FirebaseDatabase.getInstance().getReference()
            .child(uid)

        dbUsers.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.exists()){
                   Log.d(TAG, "From User: " + snapshot.getValue(User::class.java))
               }
            }
        })
    }

    // when new  message added do this
    private fun listenForMessages() {
        val fromId = mAuth.uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                Log.d(TAG, "chatMessage: " + chatMessage!!.type)

                if (chatMessage.type == MessageType.TEXT) {
                    if (chatMessage.fromId == mAuth.uid) {
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        //adapter.add(ChatToItem(chatMessage.text, currentUser))
                        adapter.add(ChatToItem(chatMessage.text, currentUser))
                    } else {
                        adapter.add(ChatFromItem(chatMessage.text, toUser!!))
                    }

                } else {
                    val imageMessage = p0.getValue(ImageMessage::class.java)

                    if (chatMessage.fromId == mAuth.uid) {
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        //adapter.add(ChatToItem(chatMessage.text, currentUser))
                        adapter.add(ImageToItem(imageMessage!!.imagePath, currentUser))
                    } else {
                        adapter.add(ImageFromItem(imageMessage!!.imagePath, toUser!!))
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
        val fromUsername = currentUser!!.username

        if (fromId == null) return

        // chat copy for sender
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        // chat copy for reciever
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        // this is the message we're going to send
        val message = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

        //Log.d(TAG, "ChatMessage: "+message)

        // setValue inerted the chat to database . . . then scroll recyclerview to the bottom
        reference.setValue(message)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(message)

        // here we are giving both users the copy of latest message
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(message)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(message)

//        // send notification
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://kotlinmessenger-3bcd8.web.app/api/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val api =
//            retrofit.create(
//                Api::class.java
//            )
//
//        val call = api.sendNotification(token, fromUsername, text)
//
//        call?.enqueue(object : Callback<ResponseBody?> {
//            override fun onResponse(
//                call: Call<ResponseBody?>,
//                response: Response<ResponseBody?>
//            ) {
//                try {
//                    toast(response.body()!!.string())
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//
//            override fun onFailure(
//                call: Call<ResponseBody?>,
//                t: Throwable
//            ) {
//            }
//        })

    }


}
