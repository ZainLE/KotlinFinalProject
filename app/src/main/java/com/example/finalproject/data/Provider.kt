package com.example.finalproject.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Provider(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val photoUrl: String = "",
    val rating: Double = 0.0,
    val hourlyRate: Double = 0.0,
    val nextAvailable: Timestamp? = null,
    val bio: String = "",
    val location: GeoPoint? = null
)
