package com.example.renjana

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class UserAdapter(val context: Context, val userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textName  = itemView.findViewById<TextView>(R.id.txtName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.textName.text = currentUser.userName

        holder.itemView.setOnClickListener{

            val postUrl = "http://api.renjanaconsulting.com/api/getFcmToken.php"
            val volleyRequestQueue = Volley.newRequestQueue(context)
            var token: String? = null
            val parameters: MutableMap<String, String> = HashMap()
            // Add your parameters in HashMap
            parameters["uid"] = currentUser.userId.toString()


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
                            token = responseObj.getString("token")
                        }else{
                            token = "is empty"
                        }
                        /*if (responseObj.has("data")) {
                            //val data = responseObj.getJSONObject("data")
                            // Handle your server response data here
                        }*/
                        Log.e("TAG :", message)

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

            val intent = Intent(context, ChatActivity::class.java)

            intent.putExtra("name", currentUser.userName)
            intent.putExtra("userId", currentUser.userId)
            intent.putExtra("token", token)

            context.startActivity(intent)
        }

    }


}