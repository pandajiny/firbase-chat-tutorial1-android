package com.example.chattutoria1android.Friends

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.R
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

class FriendsActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var currentUser: FirebaseUser
    lateinit var database: DatabaseReference
    lateinit var myUserReference: DatabaseReference

    lateinit var friendsList: ArrayList<UserProfile>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

//        init firebase
        auth = Firebase.auth
        currentUser = getAuthUser()
        database = Firebase.database.reference
        myUserReference = Firebase.database.getReference("Users/${currentUser.uid}")
//        enable offline capabilities
        myUserReference.keepSynced(true)

        friendsList = arrayListOf()

        getFriendsList()
    }


    private fun getFriendsList() {
        myUserReference.child("Profile")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.e("db error", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val myProfile = snapshot.children.first().getValue<UserProfile>()
                    if (myProfile != null) {
                        friendsList.add(myProfile!!)
                    }
                }

            })

        myUserReference.child("Friends")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.e("db error", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
//                    get Friends uid
                        val friend = it.getValue<UserProfile>()
                        if (friend != null) {
                            friendsList.add(friend)
                            Log.w("friend", friend.name)
                        } else {
                            Log.e("db error", "cannot get friends information")
                        }
                    }
                    setAdapter()
                }

            })
    }

    private fun setAdapter() {
        val viewManager = LinearLayoutManager(this)
        val viewAdapter = FriendsAdapter(friendsList, this)

        val friendsListRV = findViewById<RecyclerView>(R.id.friendsListRV)
        friendsListRV.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }

    private fun getAuthUser(): FirebaseUser {
        if (auth.currentUser != null) {
            Log.w("FriendsActivity", auth.currentUser!!.uid.toString())
            return auth.currentUser!!
        } else {
            throw error("not auth")
        }
    }
}
