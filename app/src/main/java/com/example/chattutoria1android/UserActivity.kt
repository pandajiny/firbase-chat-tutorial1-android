package com.example.chattutoria1android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.toast

class UserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
//        init Firebase
        auth = Firebase.auth
        database = Firebase.database.reference

        toast("current User :${auth.currentUser?.email}")
        setOnClickListenerEachButton()
    }

    private fun checkAuth() {
        if (auth.currentUser == null) {
            toast("Please Login First")
            logIn()
        }
    }

    private fun signOut() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            toast("Please Login First")
        } else {
            auth.signOut()
            toast("SignOut for :${currentUser.email}")
        }
    }

    private fun logIn() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            toast("User Already Logged in : ${currentUser!!.email}")
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun setOnClickListenerEachButton() {
        findViewById<Button>(R.id.userSignOutButton).setOnClickListener {
            signOut()
        }

        findViewById<Button>(R.id.userLoginButton).setOnClickListener { logIn() }
    }
}
