package com.example.chattutoria1android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.chattutoria1android.Realm.Chat
import com.example.chattutoria1android.Class.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.jetbrains.anko.toast

class ProfileActivity : AppCompatActivity() {

//    private val realm: Realm = Realm.getDefaultInstance()

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    lateinit var email: String
    lateinit var name: String
    lateinit var key: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

//        init firebase
        auth = Firebase.auth
        database = Firebase.database.reference

        email = ""
        name = ""

        getProfile()

        findViewById<Button>(R.id.profileSaveButton).setOnClickListener {
            saveMyProfile()
        }
    }

    private fun getProfile() {
        val currentUser = auth.currentUser
//        if auth user exist
        if (currentUser != null) {
            database.child("Users/${currentUser!!.uid}/Profile")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("database error", error.message)
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.key != null) {
                            key = snapshot.key!!
//                        get data from database
                            email = snapshot.child("/email").value.toString()
                            name = snapshot.child("/name").value.toString()
                        } else {
                            if (database.child("Users/${currentUser!!.uid}/Profile")
                                    .push().key != null
                            ) {
                                key =
                                    database.child("Users/${currentUser!!.uid}/Profile")
                                        .push().key!!


                            } else {
                                Log.e("key create fail", "dd")
                            }
                        }


//                        update ui with data
                        updateUI()
                    }
                })
        }
    }

    //    updating email n name text view
    private fun updateUI() {
        val emailTextView = findViewById<TextView>(R.id.myProfileEmailTextView)
        val nameTextView = findViewById<TextView>(R.id.myProfileNameTextView)

        emailTextView.text = email
        nameTextView.text = name
    }

    private fun saveMyProfile() {
//        check current User Exist
        val emailTextView = findViewById<TextView>(R.id.myProfileEmailTextView)
        val nameTextView = findViewById<TextView>(R.id.myProfileNameTextView)
        email = emailTextView.text.toString()
        name = nameTextView.text.toString()
        val currentUser = auth.currentUser
        if (currentUser != null || key != null) {
            val profile = User(currentUser!!.uid, email, name)
            database.child("Users/${currentUser!!.uid}/Profile").removeValue()

            val updates = mapOf<String, Any>(
                "Users/${currentUser.uid}/Profile" to profile.toMap()
            )

            database.updateChildren(updates)
        }
    }

//    private fun profileSave(currentUser: FirebaseUser) {

//        realm.beginTransaction()
//        realm.where<User>().equalTo("uid", currentUser.uid).findAll().deleteAllFromRealm()
//        realm.commitTransaction()
//        realm.beginTransaction()
//        val newUserProfile = realm.createObject<User>(currentUser.uid)
//        newUserProfile.name = findViewById<TextView>(R.id.nameText).text.toString()
//        newUserProfile.email = findViewById<TextView>(R.id.emailText).text.toString()
//        realm.commitTransaction()
}

//    override fun onStart() {
//    super.onStart()
//        val results = realm.where<User>().findAll()
//        val results_ = realm.where<Chat>().findAll()
//        Log.w("all users", results.toString())
//        Log.w("all logs", results_.toString())
//
////          Check Login State
//        if (auth.currentUser == null) {
////if user not logged in
//            toast("Please Login First")
//            finish()
//        } else {
////            if user exist
//            val currentUser = auth.currentUser!!
//
//            val emailText = findViewById<TextView>(R.id.emailText)
//            emailText.text = currentUser.email
//            val nameText = findViewById<TextView>(R.id.nameText)
//            toast("current login user : " + auth.currentUser!!.email)
//            nameText.text = realm.where<User>().equalTo("uid", currentUser.uid).findFirst()?.name
//            Log.w(
//                "user found!",
//                "uid:${currentUser.uid} is matched as ${realm.where<User>()
//                    .equalTo("uid", currentUser.uid).findFirst()?.name}"
//            )
//
//            val profileSaveButton = findViewById<Button>(R.id.profileSaveButton)
//            profileSaveButton.setOnClickListener {
//                profileSave(currentUser)
//            }
//        }


