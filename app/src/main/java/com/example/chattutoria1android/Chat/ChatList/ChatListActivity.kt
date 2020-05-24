package com.example.chattutoria1android.Chat.ChatList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.Database.SingleChatRoomRef
import com.example.chattutoria1android.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.tonicartos.superslim.LayoutManager
import org.jetbrains.anko.toast

class ChatListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // my single and multi chat Room's List references
    private lateinit var chatListReference: DatabaseReference

    //    local Chat Room List Var
    private lateinit var chatList: ArrayList<SingleChatRoomRef>

    private lateinit var viewAdapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

//        init auth
        auth = Firebase.auth
        checkAuth()

        chatListReference =
            Firebase.database.getReference("Users/${auth.currentUser!!.uid}/ChatRooms")

//        init local chat list val
        chatList = arrayListOf()
        viewAdapter = ChatListAdapter(chatList)

        getChatRoomList()

        setAdapter()

    }


    private fun checkAuth() {
        if (auth.currentUser == null) {
            throw error("not auth")
        }
    }

    private fun getChatRoomList() {
//        get Data from users/{currentUser}/ChatRooms/Single
        chatListReference.keepSynced(true)
        chatListReference.child("Single").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("db error", error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.w(
                    "snapshot", snapshot.getValue<Any>().toString()
                )
                snapshot.children.forEach {
//                    get each single chat room from user data
                    try {
                        val result = it.getValue<SingleChatRoomRef>()!!
                        if (!chatList.contains(result)) {
                            chatList.add(result)
                            viewAdapter.notifyDataSetChanged()
                        }
                    } catch (e: Error) {
                        Log.e("db error", e.message)
                    }
                }
            }
        })

    }

    private fun setAdapter() {
        val viewManager = LinearLayoutManager(this)
        viewManager.reverseLayout = true

        chatList.add(SingleChatRoomRef("ddd", "ddd"))

        chatList.add(SingleChatRoomRef("ddd", "ddd"))

        val chatListRV = findViewById<RecyclerView>(R.id.chatListRV)
        chatListRV.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        chatList.add(SingleChatRoomRef("ddd", "ddd"))
    }
}
