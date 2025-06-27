package com.example.finalproject.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReviewsRepository(private val reviewDao: ReviewDao) {
    open suspend fun addReview(providerId: String, text: String) {
        val review = Review(
            providerId = providerId,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        withContext(Dispatchers.IO) {
            reviewDao.insert(review)
        }
    }

    open suspend fun getReviews(providerId: String): List<Review> = withContext(Dispatchers.IO) {
        reviewDao.loadForProvider(providerId)
    }
} 