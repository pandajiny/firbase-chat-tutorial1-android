package com.example.chattutoria1android.Chat.ChatRoom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.Database.ChatLog
import com.example.chattutoria1android.Database.SingleChatRoomRef
import com.example.chattutoria1android.Friends.FriendsAdapter
import com.example.chattutoria1android.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.toast
import java.lang.NullPointerException
import java.sql.Timestamp

class ChatRoomActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

//    ChatRoom List : in Users, only List
//    ChatRooms ref : remember participates, log and everything about chat rooms

    //    my chat Rooms List
    lateinit var mySingleChatRoomsListReference: DatabaseReference

    //    current ChatRoom's key
    lateinit var currentChatRoomKey: String

    // Master Chat Room's ref, no sync
    lateinit var chatRoomsReference: DatabaseReference

    //    database reference of current Chat Room
    lateinit var currentChatRoomReference: DatabaseReference

    // local var for Rv
    lateinit var chatLogs: ArrayList<ChatLog>


    //    provided Uid : Single Chat Room
//    provided Key : Multi Chat Room
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        auth = Firebase.auth
        checkAuth()
        mySingleChatRoomsListReference =
            Firebase.database.getReference("Users/${auth.currentUser!!.uid}/ChatRooms/Single")
        mySingleChatRoomsListReference.keepSynced(true)

        chatLogs = arrayListOf()

//        get data from intent
        chatRoomsReference = Firebase.database.getReference("ChatRooms")
        val bundle = intent.extras
        val intentUid = bundle?.getString("uid")
        val intentKey = bundle?.getString("key")

//        in case of uid exist
        if (!intentUid.isNullOrEmpty()) {
            uidProvided(intentUid)
        } else if (!intentKey.isNullOrEmpty()) {
//        in case of key exist
            keyProvided(intentKey)
        }

        findViewById<FloatingActionButton>(R.id.chatRoomSendFab).setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
//        get Content Text and reset
        val contentInput = findViewById<TextView>(R.id.chatRoomContentInput)
        val content = contentInput.text.toString()
        contentInput.text = ""

        val key = currentChatRoomReference.child("ChatLogs").push().key

        val chatLog = ChatLog(auth.currentUser!!.uid, System.currentTimeMillis(), content)
        val updateMap = mapOf<String, Any>("ChatLogs/${key}" to chatLog.toMap())
        currentChatRoomReference.updateChildren(updateMap)
    }

    //    case of Multi Chat Room
    private fun keyProvided(intentKey: String) {
        TODO("not yet for multi chat room D:")
    }

    //    case of Single Chat Room
    private fun uidProvided(intentUid: String) {
//        get list of Users/{User}/ChatRooms/Single
        mySingleChatRoomsListReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                //                db error handling
                override fun onCancelled(error: DatabaseError) {
                    Log.e("db error", error.message)
                }

                //                get data snapshot
                override fun onDataChange(snapshot: DataSnapshot) {
//                    Boolean Val about already Chat Room Exist with Provided Uid(user)
                    var chatRoomExist: Boolean = false
                    snapshot.children.forEach {
//                        val result = it.getValue<SingleChatRoomRef>()
//                        if (result != null) {
//                            if (result.uid == intentUid) {
//                                currentChatRoomKey = result.chatRoomkey
//                                getChatRoomInformation(currentChatRoomKey)
//                                chatRoomExist = true
//                            }
//                        }
                        try {
                            val result = it.getValue<SingleChatRoomRef>()
                            if (result!!.uid == intentUid) {
                                currentChatRoomKey = result.chatRoomkey
                                getChatRoomInformation(currentChatRoomKey)
                                chatRoomExist = true
                            }
                        } catch (e: Error) {
                            Log.e("db error", e.message)
                        }

                    }
                    if (!chatRoomExist) {
                        createSingleChatRoomOnDb(intentUid)
                    }
                }
            })
    }


    private fun setAdapter() {
        val viewManager = LinearLayoutManager(this)
        val viewAdapter = ChatRoomLogAdapter(chatLogs)

        val chatLogRV = findViewById<RecyclerView>(R.id.chatRoomLogRV)
        chatLogRV.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun getChatRoomInformation(key: String) {
//        set current Chat Room with ChatRoomKey
        currentChatRoomReference = Firebase.database.getReference("ChatRooms/${key}")
//        keep sync for offline cap
        currentChatRoomReference.keepSynced(true)

//        get ChatLogs
        currentChatRoomReference.child("ChatLogs")
//                data for once
            .addListenerForSingleValueEvent(object : ValueEventListener {
                //                db error handling
                override fun onCancelled(error: DatabaseError) {
                    Log.e("db error", error.message)
                }

                //                get data snapshot
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
//                        get data and push to Local Val
                        val chatLog: ChatLog = try {
                            it.getValue<ChatLog>()!!
                        } catch (error: NullPointerException) {
                            Log.e("Db error", error.message)
                        } as ChatLog
//                        if no error
                        if (chatLog != null) {
                            chatLogs.add(chatLog)
                        }
                    }
//                    after receive all chat Logs, set Adapter with that data
                    setAdapter()
                }
            })
//        set listener for auto update
        currentChatRoomReference.child("ChatLogs")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        snapshot.children.last().getValue<ChatLog>()?.let { chatLogs.add(it) }
                    } catch (e: Error) {
                        Log.e("db error", e.message)
                    }
                }

            })
    }

    private fun createSingleChatRoomOnDb(uid: String) {
        val chatRoomKey = chatRoomsReference.push().key
        val key = mySingleChatRoomsListReference.push().key
        val friendKey = Firebase.database.reference.child("Users/${uid}/ChatRooms").push().key


        if (key.isNullOrEmpty() || chatRoomKey.isNullOrEmpty()) {
            throw error("cannot create key")
        }

        if (friendKey.isNullOrEmpty() || chatRoomKey.isNullOrEmpty()) {
            throw error("cannot create key")
        }

        val chatRoomRef = SingleChatRoomRef(uid, chatRoomKey)
        val myChatRoomRefUpdateMap = mapOf<String, Any>(
            key to chatRoomRef
        )

        val friendChatRoomRef = SingleChatRoomRef(auth.currentUser!!.uid, chatRoomKey)
        val friendChatRoomRefUpdateMap = mapOf<String, Any>(
            friendKey to friendChatRoomRef
        )
        Firebase.database.reference.child("Users/${uid}/ChatRooms")
            .updateChildren(friendChatRoomRefUpdateMap)

        mySingleChatRoomsListReference.updateChildren(myChatRoomRefUpdateMap)
        val chatRoomsUpdateMap = mapOf<String, String>(
            "uid1" to auth.currentUser!!.uid,
            "uid2" to uid
        )
        try {

            chatRoomsReference.child("${chatRoomKey}/Participates")
                .updateChildren(chatRoomsUpdateMap)
        } catch (e: Error) {
            Log.e("db error", e.message)
        } finally {
            getChatRoomInformation(chatRoomKey)
        }
    }

    private fun checkAuth() {
        if (auth.currentUser == null) {
            throw error("not auth")
        }
    }

}
