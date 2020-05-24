package com.example.chattutoria1android.Chat.ChatList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.Database.SingleChatRoomRef
import com.example.chattutoria1android.R

class ChatListAdapter(private var chatList: ArrayList<SingleChatRoomRef>) :
    RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {
    class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_chat_room_component, parent, false) as View
        return ChatListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.chatRoomNameText).text = chatList[position].uid
    }
}