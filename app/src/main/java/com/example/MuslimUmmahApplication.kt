package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class MuslimUmmahApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("MuslimUmmahApp", "FirebaseApp initialized successfully in Application.onCreate")
            } else {
                Log.d("MuslimUmmahApp", "FirebaseApp already initialized by content provider")
            }
        } catch (e: Throwable) {
            Log.e("MuslimUmmahApp", "Failed to initialize FirebaseApp: ${e.message}", e)
        }
    }
}
