package com.example.renjana

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class CreateAccount : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private lateinit var userName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var noHp: EditText
    private lateinit var btnAccept: Button

    private var TAG:String ?= CreateAccount::class.java.simpleName
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        supportActionBar!!.hide()

        auth = FirebaseAuth.getInstance()

        userName = findViewById(R.id.etCAname)
        email = findViewById(R.id.etCAemail)
        password = findViewById(R.id.etCApassword)
        confirmPassword = findViewById(R.id.etCArepassword)
        noHp = findViewById(R.id.etCAnohp)
        btnAccept = findViewById(R.id.btn_accept_signup)



        btnAccept.setOnClickListener {
            val userName1 = userName.text.toString()
            val email1 = email.text.toString()
            val password1 = password.text.toString()
            val confirmPassword1 = confirmPassword.text.toString()
            val noHp1 = noHp.text.toString()


            when {
                TextUtils.isEmpty(userName1) -> {
                    val snack = Snackbar.make(it, "Username is required", Snackbar.LENGTH_SHORT)
                    snack.show()
                }
                TextUtils.isEmpty(email1) -> {
                    val snack = Snackbar.make(it, "Email is required", Snackbar.LENGTH_SHORT)
                    snack.show()
                }
                TextUtils.isEmpty(noHp1) -> {
                    val snack = Snackbar.make(it, "Phonenumber is required", Snackbar.LENGTH_SHORT)
                    snack.show()
                }
                TextUtils.isEmpty(password1) -> {
                    val snack = Snackbar.make(it, "Password is required", Snackbar.LENGTH_SHORT)
                    snack.show()
                }
                password1 != confirmPassword1 -> {
                    val snack = Snackbar.make(it, "Password not match", Snackbar.LENGTH_SHORT)
                    snack.show()
                }
                else -> {
                    GlobalScope.launch(Dispatchers.IO){
                        registerUser(
                            userName1, email1, password1,
                            noHp1
                        )
                    }
                }
            }
        }

    }

    private fun registerUser(userName:String, email:String, password:String, nohp:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    val user: FirebaseUser? = auth.currentUser
                    val userId:String = user!!.uid

                    databaseReference = FirebaseDatabase.getInstance("https://renjana-882da-default-rtdb.asia-southeast1.firebasedatabase.app")
                        .getReference("Users").child(userId)

                    val hashMap:HashMap<String,String> = HashMap()
                    hashMap["userId"] = userId
                    hashMap["userName"] = userName
                    hashMap["profileImage"] = ""
                    hashMap["noHp"] = nohp
                    hashMap["password"] = password
                    hashMap["email"] = email

                    databaseReference.setValue(hashMap).addOnCompleteListener(this){ task ->
                        if(task.isSuccessful){
                            //intent

                            sendDatatoDb(userName,nohp, password, email, userId)
                            val intent = Intent(this, MainMenuActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
    }

    private fun sendDatatoDb(userName: String, noHp: String, password: String, email: String, uid: String){
        val postUrl = "http://api.renjanaconsulting.com/api/uploadUser.php"
        val volleyRequestQueue = Volley.newRequestQueue(this)
        val parameters: MutableMap<String, String> = HashMap()
        // Add your parameters in HashMap
        parameters["username"] = userName
        parameters["noHp"] = noHp
        parameters["email"] = email
        parameters["password"] = password
        parameters["uid"] = uid


        val strReq: StringRequest = object : StringRequest(
            Method.POST,postUrl,
            Response.Listener { response ->
                Log.e(TAG, "response: $response")
                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)
                    val message = responseObj.getString("message")
                    if (responseObj.has("data")) {
                        //val data = responseObj.getJSONObject("data")
                        // Handle your server response data here
                    }
                    Log.e(TAG, message)

                } catch (e: Exception) { // caught while parsing the response
                    Log.e(TAG, "problem occurred")
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
                Log.e(TAG, "problem occurred, volley error: " + volleyError.message)
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
}