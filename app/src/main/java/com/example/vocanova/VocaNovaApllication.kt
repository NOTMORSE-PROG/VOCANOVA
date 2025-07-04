package com.example.vocanova

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import android.util.Log

@HiltAndroidApp
class VocaNovaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            Log.d("VocaNovaApplication", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("VocaNovaApplication", "Error initializing Firebase", e)
        }
    }
}
