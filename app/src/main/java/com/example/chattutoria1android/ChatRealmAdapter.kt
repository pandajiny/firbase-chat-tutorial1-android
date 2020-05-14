package com.example.chattutoria1android

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.RealmBasedRecyclerViewAdapter
import io.realm.RealmResults
import io.realm.RealmViewHolder

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
        val contentTextView: TextView = itemView.findViewById(R.id.contentText)
        val timeTextView: TextView = itemView.findViewById(R.id.timeText)
    }

    override fun onBindRealmViewHolder(viewHolder: ChatRealmAdapter.ViewHolder, position: Int) {
        var chatObject = realmResults.get(position)
        viewHolder.senderTextView.text = chatObject!!.uid
        viewHolder.contentTextView.text = chatObject!!.content
        viewHolder.timeTextView.text = chatObject!!.timestamp.toString()
    }

    override fun onCreateRealmViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ChatRealmAdapter.ViewHolder {
        val view: View = inflater.inflate(R.layout.fragment_chat, viewGroup, false)
        return ViewHolder(view)
    }
}

