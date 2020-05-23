package com.example.chattutoria1android.Database

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class SingleChatRoomRef(var uid: String = "", var chatRoomkey: String = "") {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "chatRoomKey" to chatRoomkey
        )
    }
}