package com.example.renjana

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.renjana.Model.NotificationData
import com.example.renjana.Model.PushNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception



class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    var topic=""
    var receiverRoom: String? = null
    var sentRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()

        val name: String = intent.getStringExtra("name").toString()
        val receiverUid: String = intent.getStringExtra("userId").toString()
        val token: String = intent.getStringExtra("token").toString()


        sentRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.etMessageBox)
        sendButton = findViewById(R.id.sendMessageButton)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter
        //adding message to recyclerview
        mDbRef.child("chats").child(sentRoom!!).child("messages")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //getting messages in db
                    messageList.clear()
                    for(postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        //adding the message to database
        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid)

            mDbRef.child("chats").child(sentRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            messageBox.setText("")
            topic = "/topics/$token"
            PushNotification(NotificationData(name, message),
                topic).also {
                    sendNotification(it)
                    Log.d("Topic:",it.toString())
                }
        }
    }

    private fun  sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            Log.d("Response:", response.toString())
            Log.d("Notification:",notification.toString())
            if(response.isSuccessful){
                Log.d("Notification sent! :", response.toString())
            //Toast.makeText(this@ChatActivity, "Response ${Gson().toJson(response)}", Toast.LENGTH_LONG).show()
            }else{
                Log.d("Notification unsuccesfull! :", response.toString())
                //Toast.makeText(this@ChatActivity, response.errorBody().toString(), Toast.LENGTH_LONG).show()
            }
        }catch (e:Exception){
            Toast.makeText(this@ChatActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }

}