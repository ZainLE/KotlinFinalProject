package com.example.finalproject.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.finalproject.data.Review
 
// Placeholder Review entity and DAO for now
@Database(entities = [Review::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun reviewDao(): ReviewDao
} 