package com.example.chattutoria1android.Realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
open class ChatRoomRealmObject(@PrimaryKey var key: String = "", var name: String = "") :
    RealmObject() {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "key" to key,
            "name" to name
        )
    }
}


