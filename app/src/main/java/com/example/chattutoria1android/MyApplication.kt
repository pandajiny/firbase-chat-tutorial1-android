package com.example.chattutoria1android

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.realm.Realm

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
//        Realm init with Application context
        enablePersistence()
    }

    private fun enablePersistence() {
        Firebase.database.setPersistenceEnabled(true)
    }

}