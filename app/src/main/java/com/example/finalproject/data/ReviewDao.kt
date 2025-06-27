package com.example.finalproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReviewDao {
    @Insert
    fun insert(review: Review)

    @Query("SELECT * FROM reviews WHERE providerId = :id ORDER BY timestamp DESC")
    fun loadForProvider(id: String): List<Review>
} 