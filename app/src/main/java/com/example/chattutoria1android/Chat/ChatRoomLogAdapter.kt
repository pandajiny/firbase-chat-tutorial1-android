package com.example.chattutoria1android.Chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.Database.ChatLog
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.Error

class ChatRoomLogAdapter(
    private var ChatLogs: ArrayList<ChatLog>,
    private var UserList: HashMap<String, String>
) :
    RecyclerView.Adapter<ChatRoomLogAdapter.ChatLogViewHolder>() {
    class ChatLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatLogViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_chat_log_component, parent, false) as View
        return ChatLogViewHolder(
            itemView
        )
    }

    override fun getItemCount(): Int {
        return ChatLogs.size
    }

    override fun onBindViewHolder(holder: ChatLogViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.chatLogSenderText).text =
            getUserName(ChatLogs[position].uid)
        holder.itemView.findViewById<TextView>(R.id.chatLogTimestampText).text =
            ChatLogs[position].timestamp.toString()
        holder.itemView.findViewById<TextView>(R.id.chatLogContentText).text =
            ChatLogs[position].content
    }

    private fun getUserName(uid: String): String {
        return if (!UserList[uid].isNullOrEmpty()) {
            UserList[uid]!!
        } else {
            uid
        }
    }


}

