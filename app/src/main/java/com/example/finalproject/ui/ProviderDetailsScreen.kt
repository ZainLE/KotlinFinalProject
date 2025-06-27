package com.example.finalproject.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.finalproject.data.ReviewsRepository
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.finalproject.R
import com.example.finalproject.data.Provider
import com.example.finalproject.data.ProviderDetailsViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDetailsScreen(
    navController: NavController,
    providerId: String,
    viewModel: ProviderDetailsViewModel
) {
    var provider by remember { mutableStateOf<Provider?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var reviewsLoading by remember { mutableStateOf(true) }
    var reviewText by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var reviewError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    // 1️⃣ Fetch provider info
    LaunchedEffect(providerId) {
        loading = true
        error = null
        try {
            val doc = FirebaseFirestore.getInstance()
                .collection("providers")
                .document(providerId)
                .get()
                .await()
            provider = doc.toObject(Provider::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            error = e.localizedMessage
        }
        loading = false
    }

    val reviews by viewModel.reviews.collectAsState()

    // 2️⃣ Load reviews
    LaunchedEffect(providerId) {
        reviewsLoading = true
        viewModel.loadReviews()
        reviewsLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    stringResource(R.string.provider_details_title),
                    fontWeight = FontWeight.Bold
                )
            })
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Review input
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = {
                            reviewText = it
                            reviewError = null
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.write_review_hint)) },
                        singleLine = true,
                        isError = reviewError != null,
                        supportingText = {
                            reviewError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (reviewText.isBlank()) {
                                reviewError = context.getString(R.string.review_empty_error)
                                return@Button
                            }
                            submitting = true
                            viewModel.submitReview(reviewText)
                            reviewText = ""
                            submitting = false
                        },
                        enabled = reviewText.isNotBlank() && !submitting
                    ) {
                        Text(stringResource(R.string.submit))
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Loading / error states
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }
            if (error != null) {
                Text(
                    text = stringResource(R.string.error_prefix, error!!),
                    color = MaterialTheme.colorScheme.error
                )
                return@Column
            }

            // Main content
            provider?.let { p ->
                // Provider Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = p.photoUrl,
                        contentDescription = p.name,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(p.name, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text(p.category, color = Color.Gray, fontSize = 16.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RatingBar(rating = p.rating)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(R.string.hourly_rate, p.hourlyRate),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    navController.navigate("booking/$providerId")
                }) {
                    Text(stringResource(R.string.book_now))
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                Spacer(Modifier.height(8.dp))

                // Reviews section
                Text(
                    stringResource(R.string.reviews_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(8.dp))

                if (reviewsLoading) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (reviews.isEmpty()) {
                    Text(stringResource(R.string.no_reviews), color = Color.Gray)
                } else {
                    LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                        items(reviews) { review ->
                            ReviewItem(review.text, review.timestamp)
                        }
                    }
                }
            } ?: Text(
                "Provider not found",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun ReviewItem(text: String, timestamp: Long) {
    val dateFormat = stringResource(R.string.review_date_format)
    val date = remember(timestamp) {
        SimpleDateFormat(dateFormat, Locale.getDefault())
            .format(Date(timestamp))
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text)
        Text(date, color = Color.Gray, fontSize = 12.sp)
    }
}

class ProviderDetailsViewModelFactory(
    private val repo: ReviewsRepository,
    private val providerId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProviderDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProviderDetailsViewModel(repo, providerId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}