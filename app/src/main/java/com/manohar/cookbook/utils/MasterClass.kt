package com.manohar.cookbook.utils

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MasterClass : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

}