package com.example.chattutoria1android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView
import com.example.chattutoria1android.Realm.Chat
import com.example.chattutoria1android.Realm.ChatLog
import com.example.chattutoria1android.Realm.ChatRealmAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.jetbrains.anko.toast

class ChatActivity : AppCompatActivity() {

    //    init realm with default instance
//    Realm.deleteRealm()
    private lateinit var realm: Realm
//    val realm: Realm = Realm.getDefaultInstance()


    //    init firebase
    private var auth: FirebaseAuth = Firebase.auth

    private var database: DatabaseReference = Firebase.database.reference

    private val log_limit: Int = 15

    private lateinit var realmRecyclerView: RealmRecyclerView
    private lateinit var adapter: ChatRealmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        realm = Realm.getDefaultInstance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


//        Before downloading chat_log, clear all log
        clearLog()


//        get Chat log from database
//        it'll download latest {log_limit} log
        getChatLogFromDatabase(log_limit)


        //        Chat Log Display by Recycler View
        realmRecyclerView = findViewById<RealmRecyclerView>(R.id.realmRecyclerView2)


//        get Realm Data
        val realmResult = realm.where<Chat>().findAll().sort("timestamp", Sort.ASCENDING)


        startSyncChatLog()

//        set Adapter to RealmRecyclerView Object
        adapter = ChatRealmAdapter(
            this,
            realmResult,
            true
        )
        realmRecyclerView.setAdapter(adapter)
//        set the scroll position as bottom.
        scrollToEnd()
    }

    private fun scrollToEnd() {

        realmRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    override fun onStart() {
        super.onStart()
        checkAuth()
    }

    private fun clearLog() {
        realm.beginTransaction()
        realm.where<Chat>().findAll().deleteAllFromRealm()
        realm.commitTransaction()
    }

    private fun checkAuth() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            toast(currentUser.uid + " Hello!")
        } else {
            toast("Please Login First")
            finish()
        }
    }


    private fun getChatLogFromDatabase(limit: Int) {
        database.child("chats").limitToLast(limit)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.w("error", error.message.toString())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        Log.w("Snapshot for each", it.toString())
                    }



                    snapshot.children.forEach {
                        realm.beginTransaction()

                        val currentChat = it.getValue<ChatLog>()
                        val newChatLog = realm.createObject<Chat>(it.key)

                        newChatLog.uid = currentChat!!.uid
                        newChatLog.content = currentChat!!.content
                        newChatLog.timestamp = currentChat!!.timestamp

                        realm.commitTransaction()
                    }
                }

            })
    }

    //    sync with firebase Realtime DB
    private fun startSyncChatLog() {
        database.child("chats").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("databaseError", databaseError.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {


                val latestLog = dataSnapshot.children.last()

                realm.beginTransaction()
                val duplicate =
                    realm.where<Chat>().equalTo("key", latestLog.key).findAll()
                duplicate.deleteAllFromRealm()
                realm.commitTransaction()
                realm.beginTransaction()

                val newChatLog = realm.createObject<Chat>(latestLog.key)

                newChatLog.uid = latestLog.getValue<Chat>()!!.uid
                newChatLog.content = latestLog.getValue<Chat>()!!.content
                newChatLog.timestamp = latestLog.getValue<Chat>()!!.timestamp

                realm.commitTransaction()
                scrollToEnd()
            }

        })
    }

    fun sendMessage(message: String) {
//        toast("fuck")
        if (auth.currentUser != null) {
            val key = database.child("chats").push().key
            if (key == null) {
//                toast("Cannot get key for message")
            }

            val chat = Chat(
                key!!,
                auth.currentUser!!.uid,
                message,
                System.currentTimeMillis()
            )
            val chatValues = chat.toMap()

            val childUpdates = HashMap<String, Any>()
            childUpdates["/$key"] = chatValues

            database.child("chats").updateChildren(childUpdates)
//            toast("message send")

        } else {
//            toast("Please Login First")
        }
    }
}
