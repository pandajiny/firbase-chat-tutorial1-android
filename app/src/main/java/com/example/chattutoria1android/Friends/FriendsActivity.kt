package com.example.chattutoria1android.Friends

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.CustomFirebaseFunctions.CustomFirebaseFunctions
import com.example.chattutoria1android.CustomFirebaseFunctions.OnGetDataListener
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.Profile.ProfileActivity
import com.example.chattutoria1android.R
import com.example.chattutoria1android.settings.SettingsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.toast


class FriendsActivity : AppCompatActivity() {

    val functions = CustomFirebaseFunctions(this)

    lateinit var auth: FirebaseAuth

    lateinit var currentUser: FirebaseUser

    lateinit var database: DatabaseReference
    lateinit var myAccountReference: DatabaseReference

    lateinit var viewAdapter: ProfileListAdapter
    lateinit var friendsProfileList: ArrayList<UserProfile>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

//        hide action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

//        init firebase auth
        auth = Firebase.auth
        checkAuth()


//        init local friends array list
        friendsProfileList = arrayListOf()


//        get My Account Ref : "Users/uid"
        myAccountReference = Firebase.database.getReference("Users/${auth.currentUser!!.uid}")

//        get My Profile value from db and update ui
        getMyProfile()

//        get my friends list from db
        getFriendsList()

//        set adapter for friends List RV as profile list
        viewAdapter = ProfileListAdapter(friendsProfileList, this)
        setAdapter()

//        set onclick listener
        findViewById<ImageButton>(R.id.friendsSettingImageButton).setOnClickListener {
            startSettingsActivity()
        }
    }


    private fun checkAuth() {
        if (auth.currentUser == null) {
            throw error("not auth")
        }
    }


    private fun getMyProfile() {
        val myProfileReference: DatabaseReference = myAccountReference.child("Profile")
        myProfileReference.addValueEventListener(object : ValueEventListener {
            //            error handle
            override fun onCancelled(error: DatabaseError) {
                Log.e("db error", error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val myProfile = try {
                    snapshot.children.first().getValue<UserProfile>()!!
                } catch (e: Error) {
                    Log.e("db error", e.message)
                } as UserProfile
                updateMyProfileUI(myProfile)
            }
        })
        myProfileReference.keepSynced(true)
    }

    private fun updateMyProfileUI(profile: UserProfile) {
        findViewById<TextView>(R.id.friendsMyProfileNameText).text = profile.name
        findViewById<TextView>(R.id.friendsMyProfileStatusText).text = profile.status

        findViewById<ImageButton>(R.id.friendsMyProfileEditImageButton).setOnClickListener {
            startProfileActivity(profile.uid)
        }
    }


    private fun getFriendsList() {
        val myFriendsReference: DatabaseReference = myAccountReference.child("Friends")
        myFriendsReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.e("db error", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
//                    get Friends uid
                        val friend = it.getValue<UserProfile>()
                        if (friend != null) {
                            friendsProfileList.add(friend)
                            Log.w("friend", friend.name)
                        } else {
                            Log.e("db error", "cannot get friends information")
                        }
                        viewAdapter.notifyDataSetChanged()
                    }
                }
            })
        myFriendsReference.keepSynced(true)
    }


    private fun setAdapter() {
        val viewManager = LinearLayoutManager(this)

        val friendsListRV = findViewById<RecyclerView>(R.id.friendsProfileListRV)
        friendsListRV.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }


    private fun startProfileActivity(uid: String) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("uid", uid)
        startActivity(intent)
        finish()
    }

    private fun startSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        finish()
    }
}
