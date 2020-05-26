package com.example.chattutoria1android.CustomFirebaseFunctions

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.Login.LoginActivity
import com.example.chattutoria1android.Profile.ProfileEditActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

interface OnGetDataListener {
    fun onStart()
    fun onSuccess()
    fun onFailed(error: DatabaseError)
}

class CustomFirebaseFunctions(var context: Context) {
    private val auth = Firebase.auth

    //    database reference
    private lateinit var myProfileReference: DatabaseReference

    //    personal data
    var myProfile: UserProfile? = null

    init {
        checkAuth()
    }

    private fun checkAuth() {
        if (auth.currentUser == null) {
            startLoginActivity()
        }
    }

    fun startLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    }

    fun getCurrentUser(): FirebaseUser {
        checkAuth()
        return auth.currentUser!!
    }

    fun getMyAccount() {

    }

    fun getMyFriendsList() {

    }

    fun getMyChatRoomList() {

    }

    fun getChatLogs() {

    }

    fun getMyProfile(onGetDataListener: OnGetDataListener) {
        onGetDataListener.onStart()
        myProfileReference =
            Firebase.database.getReference("Users/${auth.currentUser!!.uid}/Profile")
        myProfileReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("db error", "cannot get my Profile data ${error.message}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                onGetDataListener.onSuccess()
                try {
                    val result = snapshot.children.first().getValue<UserProfile>()!!
                    myProfile = result
                } catch (e: Error) {
                    Log.e("error", e.message)
                    context.startActivity(Intent(context, ProfileEditActivity::class.java))
                }
            }
        })
        myProfileReference.keepSynced(true)
    }
}