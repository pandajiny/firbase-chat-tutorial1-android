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
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView
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

    private lateinit var db: FirebaseDatabase
    private lateinit var chatsRef: DatabaseReference

    //    init realm
    private val realm: Realm = Realm.getDefaultInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        firebase auth init
        auth = Firebase.auth
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()

//        firebase database init
        db = Firebase.database
        chatsRef = db.getReference("chats")
        chatsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                realm.beginTransaction()
                realm.deleteAll()
                realm.commitTransaction()
                dataSnapshot.children.forEach {
                    realm.beginTransaction()

                    val currentChat = it.getValue<ChatLog>()
                    val newChatLog = realm.createObject<Chat>(it.key)

                    newChatLog.uid = currentChat!!.uid
                    newChatLog.content = currentChat!!.content
                    newChatLog.timestamp = currentChat!!.timestamp

                    realm.commitTransaction()


//                    Log.w("foreach", it.getValue<ChatLog>()?.content)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("error", databaseError.message.toString())
            }
        })


//        Chat Log Display by Recycler View
        val realmRecyclerView = findViewById<RealmRecyclerView>(R.id.realmRecyclerView)


//        get Realm Data
        val realmResult = realm.where<Chat>().findAll().sort("timestamp", Sort.DESCENDING)

//        set Adapter to RealmRecyclerView Object
        val adapter = ChatRealmAdapter(this, realmResult, true)
        realmRecyclerView.setAdapter(adapter)

//        set OnClickLister to the each Button
        val signOutButton = findViewById<Button>(R.id.signOutButton)
        signOutButton.setOnClickListener {
            doSignOut()
        }

        val getUserButton = findViewById<Button>(R.id.getUserButton)
        getUserButton.setOnClickListener {
            getUserInformation()
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val sendButton = findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        if (auth.currentUser != null) {
            val key = chatsRef.push().key
            if (key == null) {
                toast("Cannot get key for message")
            }

            val contentText = findViewById<TextView>(R.id.contentInput)

            val chat = Chat(
                key!!,
                auth.currentUser!!.uid,
                contentText.text.toString(),
                System.currentTimeMillis()
            )
            val chatValues = chat.toMap()

            val childUpdates = HashMap<String, Any>()
            childUpdates["/$key"] = chatValues

            chatsRef.updateChildren(childUpdates)
            toast("message send")

            contentText.text = ""

        } else {
            toast("Please Login First")
        }
    }

    private fun updateUi() {
        TODO("not implement")
    }

    private fun doSignOut() {
        if (auth.currentUser != null) {
            val loggedInUser = auth.currentUser!!
            auth.signOut()
            toast(loggedInUser.email + "is logged out!")

        } else {
            toast("not logged in, wrong req")
        }
    }

    private fun getUserInformation() {
        if (auth.currentUser != null) {
            val user = auth.currentUser!!
            toast("user logged in is : " + user.email)
        } else {
            toast("please login first")
        }
    }
}

