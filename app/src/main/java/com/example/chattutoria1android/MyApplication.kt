package com.example.chattutoria1android

import android.app.Application
import io.realm.Realm

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
//        Realm init with Application context
        Realm.init(this)
    }

}