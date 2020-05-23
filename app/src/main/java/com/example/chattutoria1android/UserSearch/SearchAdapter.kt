package com.example.chattutoria1android.UserSearch

import android.content.Context
import android.content.Intent
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.Database.UserComponent
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.Profile.ProfileActivity
import com.example.chattutoria1android.R
import kotlinx.android.synthetic.main.fragment_user_profile_component.view.*

class SearchAdapter(private var userList: ArrayList<UserComponent>, context: Context) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>(), Filterable {
    class SearchViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    private var filteredList = ArrayList<UserComponent>()
    private var context = context

    init {
        Log.w("init", userList.toString())
        filteredList = userList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_user_profile_component, parent, false) as View
        return SearchViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }


    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.userProfileNameText).text =
            filteredList[position].name
        holder.itemView.findViewById<TextView>(R.id.userProfileNameText).setOnClickListener {
            startProfileActivityWithUid(filteredList[position].uid)
        }

    }

    private fun startProfileActivityWithUid(uid: String?) {
        if (uid != null) {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("uid", uid)
            Log.w("StartActivity", "Profile Activity with $uid")
            context.startActivity(intent)
        } else {
            throw error("not have uid")
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                Log.w("filter", "${charSearch}")
                filteredList = if (charSearch.isEmpty()) {
                    userList
                } else {
                    val resultList = ArrayList<UserComponent>()
                    for (row in userList) {
                        if (row.name!!.toLowerCase().contains(charSearch.toLowerCase())) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResult = FilterResults()
                filterResult.values = filteredList
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<UserComponent>
                notifyDataSetChanged()
            }
        }
    }

}