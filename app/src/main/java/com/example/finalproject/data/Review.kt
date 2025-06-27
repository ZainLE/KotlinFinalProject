package com.example.finalproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val providerId: String,
    val text: String,
    val timestamp: Long
) 