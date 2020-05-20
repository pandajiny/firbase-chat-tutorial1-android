package com.example.chattutoria1android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.chattutoria1android.Class.User
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

class FriendsListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)

//        init firebase
        auth = Firebase.auth
        database = Firebase.database.reference

        setMyName()

        val getFriendsListButton = findViewById<Button>(R.id.getFriendsListButton)
        getFriendsListButton.setOnClickListener {
            getFriendsList()
        }
        val addFriendButton = findViewById<Button>(R.id.addFriendButton)
        addFriendButton.setOnClickListener {
            addFriend()
        }

    }

    private fun setMyName() {
        val currentUser = getCurrentUser()
        database.child("Users/${currentUser?.uid}/Profile/name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Database Error", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val myNameText = findViewById<TextView>(R.id.myProfileNameText)
                    myNameText.text = snapshot.getValue<String>()
                }
            })
    }

    private fun getFriendsList() {
        val currentUser = getCurrentUser()
        database.child("Users/${currentUser?.uid}/FriendsList")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Database Error", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val friendsList = snapshot.getValue().toString()
                    Log.w("${currentUser?.uid}'s friends list", friendsList)
                }
            })

    }

    private fun addFriend() {
        val currentUser = getCurrentUser()
        val key = database.child("Users/${currentUser?.uid}/FriendsList").push().key

        if (key == null) {
            Log.e("database error", "key create fail")
            return
        }

        val friend = User("alicialee", "gildogi@naver.com", "Lee Hae Seon")

        val updates = mapOf<String, Any>(
            "Users/${currentUser?.uid}/FriendsList/${key}" to friend.toMap()
        )
        database.updateChildren(updates)

    }

    private fun getCurrentUser(): FirebaseUser? {
        return if (auth.currentUser != null) {
            auth.currentUser
        } else {
            toast("Please Login First")
            finish()
            null
        }
    }
}
