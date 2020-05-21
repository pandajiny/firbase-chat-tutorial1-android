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
import org.jetbrains.anko.toast

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var currentProfile: UserProfile

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

//        init firebase
        auth = Firebase.auth
        database = Firebase.database.reference

//        get auth User
        val currentUser = getAuthUser()


//        get uid from intent
        val bundle = intent.extras
//        now only can change my profile
        if (currentUser.uid != bundle?.getString("uid")) {
            throw error("cannot access")
        }

        database.child("Users/${currentUser.uid}/Profile")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.e("database error", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        currentProfile = snapshot.children.first().getValue<UserProfile>()!!
                        updateUI()
                    } else {
                        currentProfile =
                            UserProfile(
                                currentUser.uid,
                                "Your name",
                                currentUser.email!!,
                                "Your status"
                            )
                        updateUI()
                    }
                }

            })

        findViewById<FloatingActionButton>(R.id.profileEditSaveFab).setOnClickListener {
            saveUserProfile(currentUser.uid)
        }
    }

    private fun updateUI() {
        findViewById<TextView>(R.id.profileEditNameText).text = currentProfile.name
        findViewById<TextView>(R.id.profileEditEmailText).text = currentProfile.email
        findViewById<TextView>(R.id.profileEditStatusText).text = currentProfile.status
    }

    private fun getAuthUser(): FirebaseUser {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            return currentUser
        } else {
            throw error("Please Login First")
        }
    }

    private fun saveUserProfile(uid: String) {
        val newUserProfile = getUserProfileValue()
        if (newUserProfile != null) {
            database.child("Users/${uid}/Profile").removeValue()
            val key = database.child("Users/${uid}/Profile").push().key
            val updates =
                mapOf<String, Any?>("Users/${uid}/Profile/${key}" to newUserProfile.toMap())
            database.updateChildren(updates)
            toast("User Profile saved!")
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
            finish()
        }
    }

    private fun getUserProfileValue(): UserProfile? {
        name = findViewById<TextView>(R.id.profileEditNameText).text.toString()
        email = findViewById<TextView>(R.id.profileEditEmailText).text.toString()
        status = findViewById<TextView>(R.id.profileEditStatusText).text.toString()
        return if (name.isEmpty() || email.isEmpty()) {
            toast("Please fill name and email text")
            null
        } else {
            UserProfile(auth.currentUser!!.uid, name, email, status)
        }
    }
}
