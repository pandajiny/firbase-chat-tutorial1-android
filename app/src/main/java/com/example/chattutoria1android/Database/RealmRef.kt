package com.example.chattutoria1android.Database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MyProfile(
    @PrimaryKey var uid: String = "",
    var name: String = "",
    var email: String = "",
    var status: String = ""
) : RealmObject() {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "status" to status
        )
    }
}