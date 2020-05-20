package com.example.chattutoria1android.Class

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User(@PrimaryKey var uid: String = "", var email: String = "", var name: String = "") :
    RealmObject() {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "email" to email,
            "name" to name
        )
    }
}