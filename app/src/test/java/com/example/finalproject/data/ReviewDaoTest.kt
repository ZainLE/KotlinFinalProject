package com.example.finalproject.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewDaoTest {
    private lateinit var db: MyDatabase
    private lateinit var dao: ReviewDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MyDatabase::class.java).build()
        dao = db.reviewDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndQueryReviews() = runBlocking {
        val review1 = Review(providerId = "X", text = "Review 1", timestamp = 2000)
        val review2 = Review(providerId = "Y", text = "Review 2", timestamp = 3000)
        val review3 = Review(providerId = "X", text = "Review 3", timestamp = 4000)
        dao.insert(review1)
        dao.insert(review2)
        dao.insert(review3)
        val result = dao.loadForProvider("X")
        assertEquals(2, result.size)
        assertEquals("Review 3", result[0].text) // Newest first
        assertEquals("Review 1", result[1].text)
    }
} 