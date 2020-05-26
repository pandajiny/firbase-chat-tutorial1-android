package com.example.chattutoria1android.Profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.chattutoria1android.Chat.ChatActivity
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.NullPointerException

class ProfileActivity : AppCompatActivity() {

//    private val realm: Realm = Realm.getDefaultInstance()

    private lateinit var auth: FirebaseAuth
    private lateinit var accountsReference: DatabaseReference
    private lateinit var myFriendsReference: DatabaseReference

    var isMine: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

//        hide action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

//        chat require friends
        findViewById<FloatingActionButton>(R.id.profileChatFAB).hide()
//        edit require mine
        findViewById<FloatingActionButton>(R.id.profileEditFAB).hide()
//        only Add FAB default Show

//        init firebase auth
        auth = Firebase.auth
        checkAuth()
        val currentUser = auth.currentUser!!

        accountsReference = Firebase.database.getReference("Users")
        myFriendsReference = accountsReference.child("${auth.currentUser!!.uid}/Friends")


//        get uid from intent
        var profileUid: String = try {
            intent.extras!!.getString("uid")!!
        } catch (e: NullPointerException) {
//            if not, wrong req
            throw error(e.message!!)
        }

//        if auth.currentUser.uid == provided uod
        if (currentUser.uid == profileUid) {
            isMine = true
        }

//        get profile and update ui
        getProfile(profileUid)
    }

    private fun getProfile(uid: String) {
        accountsReference.child("${uid}/Profile")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.e("database error", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = try {
                        snapshot.children.first().getValue<UserProfile>()!!
                    } catch (e: Error) {
                        Log.e("db error", e.message)
                        if (isMine) {
                            requireEditProfile()
                        } else {
                            throw error("wrong request : $uid")
                        }
                    } as UserProfile
                    checkMyFriends(result.uid)
                    updateUI(result)
                }
            })
    }

    private fun checkMyFriends(uid: String) {
        myFriendsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val result = try {
                        it.getValue<UserProfile>()!!
                    } catch (e: Error) {
                        Log.e("db error", e.message)
                    } as UserProfile
                    if (result.uid == uid) {
                        findViewById<FloatingActionButton>(R.id.profileAddFAB).hide()
                        findViewById<FloatingActionButton>(R.id.profileChatFAB).show()
                    }
                }
            }

        })
    }

    private fun updateUI(profile: UserProfile) {
//        update text
        val nameText = findViewById<TextView>(R.id.profileNameText)
        nameText.text = profile.name
        val emailText = findViewById<TextView>(R.id.profileEmailText)
        emailText.text = profile.email
        val statusText = findViewById<TextView>(R.id.profileStatusText)
        statusText.text = profile.status

//        set visibility for each FAb
        if (isMine) {
//            if current profile is mine, show edit button and hide else
            findViewById<FloatingActionButton>(R.id.profileEditFAB).show()
            findViewById<FloatingActionButton>(R.id.profileAddFAB).hide()
        }


//        add click listener each FAB
        findViewById<FloatingActionButton>(R.id.profileAddFAB).setOnClickListener {
            addFriend(profile)
        }

        findViewById<FloatingActionButton>(R.id.profileChatFAB).setOnClickListener {
            startChat(profile.uid)
        }

        findViewById<FloatingActionButton>(R.id.profileEditFAB).setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            intent.putExtra(
                "uid", profile.uid
            )
            startActivity(intent)
            finish()
        }
    }

    private fun addFriend(profile: UserProfile) {
        val key = myFriendsReference.push().key
        if (!key.isNullOrEmpty()) {
            val updateMap = mapOf<String, UserProfile>(
                key to profile
            )
            myFriendsReference.updateChildren(updateMap)
        } else {
            Log.e("db error", "cannot get db push key")
        }
    }

    private fun startChat(uid: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    //    only for mine
    private fun requireEditProfile() {
        val intent = Intent(this, ProfileEditActivity::class.java)
        intent.putExtra("uid", auth.currentUser!!.uid)
        startActivity(intent)
        finish()
    }

    private fun checkAuth() {
        if (auth.currentUser == null) {
            throw error("not auth")
        }
    }
}