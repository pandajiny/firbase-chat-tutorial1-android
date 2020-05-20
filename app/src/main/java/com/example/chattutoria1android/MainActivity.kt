package com.example.chattutoria1android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.toast
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        firebase init
        auth = Firebase.auth
        database = Firebase.database.reference

        checkUser()
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
                        val name: String? = snapshot.child("/name").getValue<String>()
                        val email: String? = snapshot.child("/email").getValue<String>()
                        if (email == null || name == null) {
                            toast("Please Fill the Profile Form")
                            Log.w("Profile Empty", "${currentUser!!.uid} has no Profile!")
                            startProfileActivity()
                        }
                    }

                })
        }
    }

    private fun startProfileActivity() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }
}

