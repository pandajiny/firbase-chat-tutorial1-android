package com.example.chattutoria1android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.toast
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.Friends.FriendsActivity
import com.example.chattutoria1android.Login.LoginActivity
import com.example.chattutoria1android.Profile.ProfileEditActivity
import com.example.chattutoria1android.UserSearch.UserSearchActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

//        init Firebase
        auth = Firebase.auth
        database = Firebase.database.reference
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
//            Auth OK
            getUserFromDB()
        } else {
//            Auth Fail
            startLoginActivity()
        }
    }

    private fun getUserFromDB() {
        val myUserReference = database.child("Users/${auth.currentUser!!.uid}")
//        get My Profile
        myUserReference.child("Profile")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
//                        profile exist
                        val myProfile = dataSnapshot.getValue<UserProfile>()
                        if (myProfile is UserProfile) {
                            updateUI(myProfile)
                            startFriendsActivity()
                        } else {
                            toast("Profile Data Broken!")
//                            error handling
                        }
                    } else {
//                        profile doesn't exist
//                        force move to profile edit page
                    }
                }
            })
    }

    private fun updateUI(profile: UserProfile) {
        findViewById<TextView>(R.id.mainWelcomeText).text = "Welcome ${profile.name}"
    }

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun startFriendsActivity() {
        startActivity(Intent(this, FriendsActivity::class.java))
        finish()
    }
}