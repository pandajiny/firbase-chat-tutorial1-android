package com.example.chattutoria1android.Friends

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.Chat.ChatRoom.ChatRoomActivity
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.R
import kotlinx.android.synthetic.main.fragment_user_profile_component.view.*

class FriendsAdapter(private var friendsList: ArrayList<UserProfile>, context: Context) :
    RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {
    class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_user_profile_component, parent, false) as View
        return FriendsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.userProfileNameText).text =
            friendsList[position].name

        holder.itemView.findViewById<TextView>(R.id.userProfileNameText).setOnClickListener {
            startChatRoom(
                friendsList[position].uid
            )
        }
    }

    private fun startChatRoom(uid: String) {
        val intent = Intent(context, ChatRoomActivity::class.java)
        intent.putExtra("uid", uid)
        context.startActivity(intent)
    }
}