package com.example.chattutoria1android.Chat.ChatRoom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.Database.ChatLog
import com.example.chattutoria1android.R

class ChatRoomLogAdapter(private var ChatLogs: ArrayList<ChatLog>) :
    RecyclerView.Adapter<ChatRoomLogAdapter.ChatLogViewHolder>() {
    class ChatLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatLogViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_chat_log_component, parent, false) as View
        return ChatLogViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return ChatLogs.size
    }

    override fun onBindViewHolder(holder: ChatLogViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.chatLogSenderText).text = ChatLogs[position].uid
        holder.itemView.findViewById<TextView>(R.id.chatLogTimestampText).text =
            ChatLogs[position].timestamp.toString()
        holder.itemView.findViewById<TextView>(R.id.chatLogContentText).text =
            ChatLogs[position].content
    }

}