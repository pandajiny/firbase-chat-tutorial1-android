package com.example.chattutoria1android.Database

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserProfile(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var status: String = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "status" to status
        )
    }
}

@IgnoreExtraProperties
data class UserComponent(
    var uid: String? = "",
    var name: String? = "",
    val email: String? = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "email" to email
        )
    }
}