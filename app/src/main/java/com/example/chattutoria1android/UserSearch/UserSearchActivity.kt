package com.example.chattutoria1android.UserSearch

import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattutoria1android.Database.UserComponent
import com.example.chattutoria1android.Database.UserProfile
import com.example.chattutoria1android.R
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
import kotlinx.android.synthetic.main.activity_user_search.*
import java.lang.Error


class UserSearchActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_search)

        auth = Firebase.auth
        database = Firebase.database.reference

        currentUser = getAuthUser()

        val userList = arrayListOf<UserComponent>()
        database.child("Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("database error", error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val userProfile = it.child("Profile").children.first()?.getValue<UserProfile>()
                    userList.add(UserComponent(userProfile?.uid, userProfile?.name))
                }
            }
        })


        val viewManager = LinearLayoutManager(this)
        val viewAdapter = SearchAdapter(userList, this)

        val searchRV = findViewById<RecyclerView>(R.id.user_search_rv)


        val user_search = findViewById<SearchView>(R.id.user_search)
        user_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewAdapter.filter.filter(newText)
                return false
            }
        })

        searchRV.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }

    private fun getAuthUser(): FirebaseUser {
        if (auth.currentUser != null) {
            return auth.currentUser!!
        } else {
            throw Error("Cannot find User")
        }
    }
}
