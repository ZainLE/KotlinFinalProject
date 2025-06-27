package com.example.finalproject

import android.app.Application
import androidx.room.Room
import com.example.finalproject.data.MyDatabase

class FinalProjectApp : Application() {
    lateinit var database: MyDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            MyDatabase::class.java,
            "my_database.db"
        ).build()
    }
} 