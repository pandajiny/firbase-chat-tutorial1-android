package com.example.chattutoria1android.Profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

//    private val realm: Realm = Realm.getDefaultInstance()

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    lateinit var currentProfile: UserProfile

    lateinit var name: String
    lateinit var email: String
    lateinit var status: String
    lateinit var key: String

    var isMine: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

//        init firebase
        auth = Firebase.auth
        database = Firebase.database.reference

//        get Auth User
        val currentUser = getAuthUser()

//        get uid from intent
        var bundle = intent.extras
        if (bundle?.getString("uid") != null) {
//            get Profile from intent's uid
            if (currentUser.uid == bundle.getString("uid")!!) {
                isMine = true
            }
            getProfile(bundle.getString("uid")!!)
        } else {
//            if there's no uid
            throw error("not have uid intent")
        }


        val addFab = findViewById<FloatingActionButton>(R.id.profileAddFab)
        addFab.setOnClickListener {
            val key = database.child("Users/${currentUser.uid}/FriendsList").push().key
            val updateMap = mapOf<String, String>(
                "Users/${auth.currentUser!!.uid}/FriendsList/${key}" to currentProfile.uid
            )

            database.updateChildren(updateMap)
        }

        val editFab = findViewById<FloatingActionButton>(R.id.profileEditFab)
        editFab.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            intent.putExtra(
                "uid", currentUser.uid
            )
            startActivity(intent)
            finish()
        }
    }

    private fun updateUI() {
        val nameText = findViewById<TextView>(R.id.profileNameText)
        nameText.text = currentProfile.name
        val emailText = findViewById<TextView>(R.id.profileEmailText)
        emailText.text = currentProfile.email
        val statusText = findViewById<TextView>(R.id.profileStatusText)
        statusText.text = currentProfile.status
    }


    private fun getProfile(uid: String) {
        if (auth.currentUser != null) {
            database.child("Users/${uid}/Profile")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("database error", error.message)
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        when {
                            snapshot.exists() -> {
                                currentProfile = snapshot.children.first().getValue<UserProfile>()!!
                                updateUI()
                            }
                            isMine -> {
                                requireEditProfile()
                            }
                            else -> {
                                throw error("Wrong request, user doesn't have a profile")
                            }
                        }
                        Log.w("currentProfile", snapshot.children.first().getValue().toString())
                    }
                })
        }
    }

    //    only for mine
    private fun requireEditProfile() {
        val intent = Intent(this, ProfileEditActivity::class.java)
        intent.putExtra("uid", auth.currentUser!!.uid)
        startActivity(intent)
        finish()
    }

    private fun getAuthUser(): FirebaseUser {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            return currentUser
        } else {
            throw error("Please Login First")
        }
    }
}