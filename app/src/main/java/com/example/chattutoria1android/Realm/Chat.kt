package com.example.chattutoria1android.Realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Chat(
    @PrimaryKey var key: String = "",
    var uid: String = "",
    var content: String = "",
    var timestamp: Long = 0
) : RealmObject() {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "key" to key,
            "uid" to uid,
            "content" to content,
            "timestamp" to timestamp
        )
    }
}

public class ChatLog {
    public lateinit var uid: String
    public lateinit var content: String
    public var timestamp: Long = 0

}