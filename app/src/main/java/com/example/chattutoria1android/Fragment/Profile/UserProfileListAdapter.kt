package com.example.chattutoria1android.Fragment.Profile

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.chattutoria1android.Class.User
import com.example.chattutoria1android.R
import io.realm.RealmBasedRecyclerViewAdapter
import io.realm.RealmResults
import io.realm.RealmViewHolder

// can be replaced to ProfileListAdapter
class UserProfileListAdapter(
    context: Context,
    realmResults: RealmResults<User>,
    automaticUpdate: Boolean
) :
    RealmBasedRecyclerViewAdapter<User, UserProfileListAdapter.ViewHolder>(
        context,
        realmResults,
        automaticUpdate,
        false
    ) {
    class ViewHolder(itemView: View) : RealmViewHolder(itemView) {
        val profileNameTextView: TextView = itemView.findViewById<TextView>(R.id.myProfileNameTextView)

    }

    override fun onBindRealmViewHolder(viewHolder: ViewHolder, position: Int) {
        var profileObject = realmResults[position]
        viewHolder.profileNameTextView.text = profileObject!!.name
    }


    override fun onCreateRealmViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            inflater.inflate(R.layout.fragment_user_profile_component, viewGroup, false)
        return ViewHolder(
            view
        )
    }
}

