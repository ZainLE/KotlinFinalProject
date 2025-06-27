package com.example.finalproject.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FakeReviewsRepository(private val reviews: List<Review>) : ReviewsRepository(dao = null!!) {
    override suspend fun getReviews(providerId: String): List<Review> {
        return reviews.filter { it.providerId == providerId }
    }
    override suspend fun addReview(providerId: String, text: String) { /* no-op */ }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProviderDetailsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ProviderDetailsViewModel
    private lateinit var fakeRepo: FakeReviewsRepository
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        val reviews = listOf(
            Review(providerId = "X", text = "A", timestamp = 1),
            Review(providerId = "Y", text = "B", timestamp = 2),
            Review(providerId = "X", text = "C", timestamp = 3)
        )
        fakeRepo = FakeReviewsRepository(reviews)
        viewModel = ProviderDetailsViewModel(fakeRepo, "X")
    }

    @Test
    fun loadReviews_returnsCorrectList() = testScope.runTest {
        viewModel.loadReviews()
        val result = viewModel.reviews.value
        assertEquals(2, result.size)
        assertEquals("A", result[0].text)
        assertEquals("C", result[1].text)
    }
} 