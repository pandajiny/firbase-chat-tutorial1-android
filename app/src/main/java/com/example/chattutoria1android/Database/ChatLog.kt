package com.example.chattutoria1android.Database

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChatLog(var uid: String = "", var timestamp: Long = 0, var content: String = "") {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "timestamp" to timestamp,
            "content" to content
        )
    }
}