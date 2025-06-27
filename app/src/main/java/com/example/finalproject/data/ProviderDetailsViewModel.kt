package com.example.finalproject.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProviderDetailsViewModel(
    private val repository: ReviewsRepository,
    private val providerId: String
) : ViewModel() {
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    fun loadReviews() {
        viewModelScope.launch {
            _reviews.value = repository.getReviews(providerId)
        }
    }

    fun submitReview(text: String) {
        viewModelScope.launch {
            repository.addReview(providerId, text)
            loadReviews() // Refresh after adding
        }
    }
} 