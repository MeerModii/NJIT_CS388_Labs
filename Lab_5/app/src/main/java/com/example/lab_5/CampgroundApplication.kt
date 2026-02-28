package com.example.lab_5

import android.app.Application

class CampgroundApplication : Application() {
    val db by lazy { AppDatabase.getInstance(this) }
}
