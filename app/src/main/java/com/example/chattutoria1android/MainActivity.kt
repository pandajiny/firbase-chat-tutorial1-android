package com.example.chattutoria1android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.toast
import android.util.Log
import android.widget.Toast
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.Login.LoginActivity
import com.example.chattutoria1android.Profile.ProfileEditActivity
import com.example.chattutoria1android.UserSearch.UserSearchActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private var functions =
        com.example.chattutoria1android.CustomFirebaseFunctions.CustomFirebaseFunctions(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        test code

//        test code end

//        hide Action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

//        Toast("Welcome! ${functions.getMyProfile().}")

        findViewById<Button>(R.id.mainSearchButton).setOnClickListener {
            startActivity(Intent(this, UserSearchActivity::class.java))
        }
    }

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}