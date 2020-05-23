package com.example.chattutoria1android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.toast
import android.util.Log
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.Login.LoginActivity
import com.example.chattutoria1android.Profile.ProfileActivity
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

//        firebase init
        auth = Firebase.auth
        database = Firebase.database.reference

        checkUser()

        findViewById<Button>(R.id.mainSearchButton).setOnClickListener {
            startActivity(Intent(this, UserSearchActivity::class.java))
        }

    }

    private fun checkUser() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
//            require login first
            toast("Please Login first")
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            database.child("Users/${currentUser.uid}/Profile")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("database error", error.message)
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Log.w(
                                "currentUser",
                                snapshot.children.first().getValue<UserProfile>().toString()
                            )
                        } else {
                            startMyProfileEditActivity()
                        }
                    }

                })
        }
    }

    private fun startMyProfileEditActivity() {
        val intent = Intent(this, ProfileEditActivity::class.java)
        intent.putExtra("uid", auth.currentUser!!.uid)
        startActivity(intent)
    }
}