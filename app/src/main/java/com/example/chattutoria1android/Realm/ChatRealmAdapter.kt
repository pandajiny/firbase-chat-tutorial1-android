package com.example.chattutoria1android.Realm

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.chattutoria1android.Class.User
import com.example.chattutoria1android.R
import io.realm.Realm
import io.realm.RealmBasedRecyclerViewAdapter
import io.realm.RealmResults
import io.realm.RealmViewHolder
import io.realm.kotlin.where
import java.lang.Long.parseLong
import java.text.SimpleDateFormat
import java.util.*

class ChatRealmAdapter(
    context: Context,
    realmResults: RealmResults<Chat>,
    automaticUpdate: Boolean
) :
    RealmBasedRecyclerViewAdapter<Chat, ChatRealmAdapter.ViewHolder>(
        context,
        realmResults,
        automaticUpdate,
        false
    ) {
    class ViewHolder(itemView: View) : RealmViewHolder(itemView) {
        val senderTextView: TextView = itemView.findViewById(R.id.senderText)
        val contentTextView: TextView = itemView.findViewById(R.id.contentInputText)
        val timeTextView: TextView = itemView.findViewById(R.id.timeText)
    }

    override fun onBindRealmViewHolder(viewHolder: ViewHolder, position: Int) {
        var chatObject = realmResults[position]
        viewHolder.senderTextView.text = getSenderName(chatObject!!.uid)
        viewHolder.contentTextView.text = chatObject!!.content
        viewHolder.timeTextView.text = getDateTime(chatObject!!.timestamp.toString())
    }

    private fun getSenderName(uid: String): String {
        val realm: Realm = Realm.getDefaultInstance()
        val searchResult = realm.where<User>().equalTo("uid", uid).findFirst()?.name

        return if (searchResult != null) {
            searchResult
        } else {
            Log.e("Cannot find user name", uid + "is not saved in db")
            uid
        }
    }

    override fun onCreateRealmViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = inflater.inflate(R.layout.fragment_chat, viewGroup, false)
        return ViewHolder(
            view
        )
    }

    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("MM/dd")
            val netDate = Date(parseLong(s))
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }
}

