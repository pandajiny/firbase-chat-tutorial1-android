package com.example.chattutoria1android.Friends

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.Profile.ProfileActivity
import com.example.chattutoria1android.R

class ProfileListAdapter(private var userProfileList: ArrayList<UserProfile>, context: Context) :
    RecyclerView.Adapter<ProfileListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_profile_list_component, parent, false) as View
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userProfileList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.profileListComponentProfileNameText).text =
            userProfileList[position].name

        holder.itemView.findViewById<TextView>(R.id.profileListComponentProfileStatusText).text =
            userProfileList[position].status

        holder.itemView.setOnClickListener {
            startProfileActivity(
                userProfileList[position].uid
            )
        }
    }

    private fun startProfileActivity(uid: String) {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra("uid", uid)
        context.startActivity(intent)
    }
}