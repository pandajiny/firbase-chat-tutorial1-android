package com.example.chattutoria1android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.toast
import android.util.Log
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        firebase auth init
        auth = Firebase.auth

        val signOutButton = findViewById<Button>(R.id.signOutButton)
        signOutButton.setOnClickListener {
            doSignOut()
        }

        val getUserButton = findViewById<Button>(R.id.getUserButton)
        getUserButton.setOnClickListener {
            getUserInformation()
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun doSignOut() {
        if (auth.currentUser != null) {
            val loggedInUser = auth.currentUser!!
            auth.signOut()
            toast(loggedInUser.email + "is logged out!")

        } else {
            toast("not logged in, wrong req")
        }
    }

    private fun getUserInformation() {
        if (auth.currentUser != null) {
            val user = auth.currentUser!!
            toast("user logged in is : " + user.email)
        } else {
            toast("please login first")
        }
    }
}

