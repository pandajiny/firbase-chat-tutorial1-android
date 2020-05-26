package com.example.chattutoria1android.ChatList

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.Chat.ChatActivity
import com.example.chattutoria1android.Database.SingleChatRoomRef
import com.example.chattutoria1android.R

class ChatListAdapter(private var chatList: ArrayList<SingleChatRoomRef>, val context: Context) :
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
        holder.itemView.findViewById<TextView>(R.id.chatListComponentNameText).text =
            chatList[position].uid
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("uid", chatList[position].uid)
            context.startActivity(intent)
        }
    }
}