package com.example.renjana

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser



class LoginActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private  var firebaseUser: FirebaseUser? = null

    private lateinit var btnLogin: Button
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnCreateAcc: Button
    private lateinit var constraintLayout: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()

        auth = FirebaseAuth.getInstance()
        //firebaseUser = auth!!.currentUser!!

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnCreateAcc = findViewById(R.id.btnCreateAcc)
        constraintLayout = findViewById(R.id.constraintLayout)

        //check if user login then navigate to user screen
        if (firebaseUser !== null) {
            val intent = Intent(
                this,
                MainMenuActivity::class.java
            )
            startActivity(intent)
            finish()
        }
        btnLogin.setOnClickListener{
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()


            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                val snack = Snackbar.make(
                    constraintLayout,
                    "Email/password is required",
                    Snackbar.LENGTH_SHORT
                )
                snack.show()
            }else{
                auth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            etEmail.setText("")
                            etPassword.setText("")
                            val intent = Intent(this, MainMenuActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val snack = Snackbar.make(
                                constraintLayout,
                                "Email/password is wrong",
                                Snackbar.LENGTH_SHORT
                            )
                            snack.show()
                        }
                    }
            }
        }

        btnCreateAcc.setOnClickListener {
            val intent = Intent (this, CreateAccount::class.java)
            startActivity(intent)
        }
    }
}


