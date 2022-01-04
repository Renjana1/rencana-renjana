package com.example.renjana

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.renjana.firebase.FirebaseService
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject

class PreChatActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var constraintLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_chat)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseService.token = it
            Log.d("Token it:",it.toString())
            addFcmToken(FirebaseService.token, mAuth.currentUser?.uid)
        }

        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        constraintLayout = findViewById(R.id.mainMenuConstraintLayout)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)

        userRecyclerView.adapter = adapter

        mDbRef.child("Users").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    //to remove himself from recyclerview
                    if(mAuth.currentUser?.uid != currentUser?.userId){
                        userList.add(currentUser!!)
                    }

                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                val snack = Snackbar.make(
                    constraintLayout,
                    error.message,
                    Snackbar.LENGTH_LONG
                )
                snack.show()
            }

        })

    }

    private fun addFcmToken(token: String?, uid: String?) {
        val postUrl = "http://api.renjanaconsulting.com/api/uploadFcmToken.php"
        val volleyRequestQueue = Volley.newRequestQueue(this)
        val parameters: MutableMap<String, String> = HashMap()
        // Add your parameters in HashMap
        parameters["token"] = token.toString()
        parameters["uid"] = uid.toString()


        val strReq: StringRequest = object : StringRequest(
            Method.POST,postUrl,
            Response.Listener { response ->
                Log.e("TAG :", "response: $response")
                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)
                    val message = responseObj.getString("message")
                    val success = responseObj.getString("success")
                    if(success == "1"){
                        Log.e("TAG :", message)
                    }else{
                        Log.e("TAG :", message)
                    }
                   /* if (responseObj.has("data")) {
                        //val data = responseObj.getJSONObject("data")
                        // Handle your server response data here
                    }*/
                } catch (e: Exception) { // caught while parsing the response
                    Log.e("TAG :", "problem occurred")
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
                Log.e("TAG :", "problem occurred, volley error: " + volleyError.message)
            }) {

            override fun getParams(): MutableMap<String, String> {
                return parameters
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers: MutableMap<String, String> = HashMap()
                // Add your Header paramters here
                return headers
            }


            }
            volleyRequestQueue.add(strReq)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuLogout){
            mAuth.signOut()
            val intent = Intent (this@PreChatActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return true
    }
}