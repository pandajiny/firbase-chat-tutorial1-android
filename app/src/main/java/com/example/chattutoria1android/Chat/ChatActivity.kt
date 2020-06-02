package com.example.chattutoria1android.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.Database.ChatLog
import com.example.chattutoria1android.Database.SingleChatRoomRef
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.NullPointerException

class ChatActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    //    current ChatRoom's key
    lateinit var currentChatRoomKey: String

    // local var for Rv
    lateinit var chatLogs: ArrayList<ChatLog>
    lateinit var userList: HashMap<String, String>

    lateinit var viewAdapter: ChatRoomLogAdapter
    lateinit var viewManager: LinearLayoutManager


    //    provided Uid : Single Chat Room
//    provided Key : Multi Chat Room
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

//        hide action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

//        init firebase auth
        auth = Firebase.auth
        database = Firebase.database.reference


//      set Adapter
        chatLogs = arrayListOf()
//        uid, name
        userList = hashMapOf()

        viewAdapter = ChatRoomLogAdapter(chatLogs, userList)
        viewManager = LinearLayoutManager(this)

        getUserList()

        val chatLogRV = findViewById<RecyclerView>(R.id.chatRoomLogRV)
        chatLogRV.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
//        end set Adapter


        findViewById<ImageButton>(R.id.chatRoomSendImageButton).setOnClickListener {
            sendMessage()
        }
    }

    override fun onStart() {
        super.onStart()
//        check auth
        if (auth.currentUser == null) {
            finish()
        }

//        check single chat room or multi chat room or else:error
        intent.extras.let {
            if (!it?.getString("uid").isNullOrEmpty()) {
                uidProvided(it?.getString("uid")!!)
            } else if (!it?.getString("key").isNullOrEmpty()) {
                keyProvided(it?.getString("key")!!)
            } else {
                throw error("not provided uid or key intent")
            }
        }
    }

    private fun sendMessage() {
//        get Content Text and reset
        val currentChatRoomReference = database.child("ChatRooms/${currentChatRoomKey}")
        val key = currentChatRoomReference.child("ChatLogs").push().key


        val contentInput = findViewById<TextView>(R.id.chatRoomContentInput)
        val content = contentInput.text.toString()
        contentInput.text = ""


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
        val mySingleChatRoomsListReference =
            database.child("Users/${auth.currentUser!!.uid}/ChatRooms/Single")

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
                    snapshot.children.forEach {
                        try {
//                            search chat room with me and provided uid
                            val result = it.getValue<SingleChatRoomRef>()
                            if (result!!.uid == intentUid) {
                                currentChatRoomKey = result.chatRoomkey
                                getChatRoomInformation(currentChatRoomKey)
                                return
                            }
                        } catch (e: Error) {
//                            error handling
                            Log.e("db error", e.message)
                        }

                    }
                    val uidList = arrayListOf<String>()
                    uidList.add(auth.currentUser!!.uid)
                    uidList.add(intentUid)
                    createSingleChatRoomOnDb(uidList)
                }
            })
//        enable offline capability
        mySingleChatRoomsListReference.keepSynced(true)
    }

    private fun getChatRoomInformation(key: String) {
//        set current Chat Room with ChatRoomKey
        val currentChatRoomReference = Firebase.database.getReference("ChatRooms/${key}")

        currentChatRoomReference.child("ChatLogs")
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    val chatLog = dataSnapshot.getValue<ChatLog>()
                    if (chatLog is ChatLog) {
                        chatLogs.add(chatLog)
                        viewAdapter.notifyDataSetChanged()
//                        not worked
                        viewManager.scrollToPosition(chatLogs.size)
                    }
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
//                    val deleted = dataSnapshot.getValue<ChatLog>()
//                    if(deleted is ChatLog){
//
//                    }
                }
            })
        currentChatRoomReference.keepSynced(true)
    }

    private fun createSingleChatRoomOnDb(uidList: ArrayList<String>) {
        val key = database.child("ChatRooms").push().key

        if (key is String) {
//        set chat room detail
            uidList.forEach {
//            set members for chat room will be created
                val currentUserKey = database.child("ChatRooms/${key}/Members").push().key
                if (currentUserKey is String) {
                    val updates = mapOf<String, String>(currentUserKey to it)
                    database.child("ChatRooms/${key}/Members").updateChildren(updates)
                }

//            set for each member
                if (uidList.size == 2) {
//                    for me
                    if (auth.currentUser?.uid == it) {
                        val currentKey =
                            database.child("Users/${auth.currentUser?.uid}/ChatRooms/Single")
                                .push().key
                        val updates = mapOf<String, SingleChatRoomRef>(
                            "Users/${auth.currentUser?.uid}/ChatRooms/Single${currentKey}" to SingleChatRoomRef(
                                uidList.last(), key
                            )
                        )
                        database.updateChildren(updates)
                    } else {
//                        for friends
                        val currentKey =
                            database.child("Users/${it.last()}/ChatRooms/Single")
                                .push().key
                        val updates = mapOf<String, SingleChatRoomRef>(
                            "Users/${auth.currentUser?.uid}/ChatRooms/Single${currentKey}" to SingleChatRoomRef(
                                uidList.first(), key
                            )
                        )
                        database.updateChildren(updates)
                            .addOnCompleteListener { getChatRoomInformation(key) }
                    }
                }
            }
        }
    }

    private fun getUserList() {
//        need my name
        userList[auth.currentUser!!.uid] = "ME"
        val myFriendsReference = database.child("Users/${auth.currentUser!!.uid}/Friends")
        myFriendsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    val friend = it.getValue<UserProfile>()
                    if (friend is UserProfile) {
                        userList[friend.uid] = friend.name
                        viewAdapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }
}
