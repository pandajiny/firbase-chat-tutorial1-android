package com.example.chattutoria1android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chattutoria1android.Realm.ChatRoomRealmObject
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.toast

class ChatListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

//        init firebase
        auth = Firebase.auth


//        set onClickListener to newChatRoomFab
        val newChatRoomFab = findViewById<FloatingActionButton>(R.id.newChatRoomFab)
        newChatRoomFab.setOnClickListener {
            createChatRoom()
        }
    }

    private fun createChatRoom() {
        val database: DatabaseReference = Firebase.database.reference
        val auth = Firebase.auth
        if (auth.currentUser != null) {
            val key = database.child("ChatList").push().key
            Log.w("data key", key)


            val newChatRoom = ChatRoomRealmObject(key!!, "With Ali").toMap()
            val childUpdates = HashMap<String, Any>()
            childUpdates["/$key"] = newChatRoom

            database.child("ChatList").updateChildren(childUpdates)
        }
    }
}
