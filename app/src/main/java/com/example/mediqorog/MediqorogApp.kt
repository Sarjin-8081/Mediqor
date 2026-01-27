package com.example.mediqorog

import android.app.Application
import com.google.firebase.FirebaseApp

class MediQorogApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}