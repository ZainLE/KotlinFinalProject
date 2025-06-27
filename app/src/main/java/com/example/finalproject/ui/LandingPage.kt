package com.example.finalproject.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.R
import coil3.compose.AsyncImage
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ViewList
import com.example.finalproject.data.Provider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.data.ProviderViewModel
import androidx.navigation.NavController

// --- Data Model ---



val categories = listOf("Plumbing", "Electrical", "Carpentry", "Cleaning", "Painting")

// --- Main Landing Page ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingPage(navController: NavController) {
    val providerViewModel: ProviderViewModel = viewModel()
    val providers by providerViewModel.providers.collectAsState()

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isMapView by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ServicePro", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Add booking action */ }) {
                Icon(Icons.Default.Add, contentDescription = "Book")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CategoryChips(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
            Spacer(Modifier.height(8.dp))
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MapPinButton(onClick = { /* TODO: Use GPS location */ })
                ToggleButton(
                    isMapView = isMapView,
                    onToggle = { isMapView = !isMapView }
                )
            }
            Spacer(Modifier.height(8.dp))
            if (isMapView) {
                MapView()
            } else {
                ProviderList(
                    providers = providers
                        .filter { selectedCategory == null || it.category == selectedCategory }
                        .filter { it.name.contains(searchQuery, ignoreCase = true) },
                    navController = navController
                )
            }
        }
    }
}

// --- Category Chips ---
@Composable
fun CategoryChips(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp)
    ) {
        AssistChip(
            onClick = { onCategorySelected(null) },
            label = { Text(stringResource(R.string.all_categories)) },
            colors = if (selectedCategory == null) AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primary, labelColor = Color.White) else AssistChipDefaults.assistChipColors()
        )
        Spacer(Modifier.width(8.dp))
        categories.forEach { category ->
            AssistChip(
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                colors = if (selectedCategory == category) AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primary, labelColor = Color.White) else AssistChipDefaults.assistChipColors()
            )
            Spacer(Modifier.width(8.dp))
        }
    }
}

// --- Search Bar ---
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.search_providers)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        singleLine = true,
        shape = RoundedCornerShape(24.dp)
    )
}

// --- Map Pin Button ---
@Composable
fun MapPinButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Default.LocationOn, contentDescription = stringResource(R.string.use_my_location))
    }
}

// --- Toggle Button (List/Map) ---
@Composable
fun ToggleButton(isMapView: Boolean, onToggle: () -> Unit) {
    OutlinedButton(
        onClick = onToggle,
        shape = CircleShape
    ) {
        Icon(
            imageVector = if (isMapView) Icons.Default.ViewList else Icons.Default.Map,
            contentDescription = if (isMapView) stringResource(R.string.show_list) else stringResource(R.string.show_map)
        )
    }
}

// --- Provider List ---
@Composable
fun ProviderList(providers: List<Provider>, navController: NavController) {
    if (providers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_providers_found))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            providers.forEach { provider ->
                ProviderCard(provider, navController)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

// --- Provider Card ---
@Composable
fun ProviderCard(provider: Provider, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { navController.navigate("provider/${provider.id}") },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = provider.photoUrl,
                contentDescription = provider.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(provider.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(provider.category, color = Color.Gray, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingBar(rating = provider.rating)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.hourly_rate, provider.hourlyRate), fontWeight = FontWeight.Medium)
                }
                Text(stringResource(R.string.next_available, provider.nextAvailable ?: "N/A"), color = Color(0xFF388E3C), fontSize = 13.sp)
            }
        }
    }
}

// --- Simple Rating Bar ---
@Composable
fun RatingBar(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(rating.toInt()) {
            Icon(
                painter = painterResource(id = R.drawable.ic_halfstar), // Use the new XML name
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(16.dp)
            )
        }
        if (rating - rating.toInt() >= 0.5) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.StarHalf,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// --- Map View Placeholder ---
@Composable
fun MapView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("[Map View Placeholder]", color = Color.Gray)
    }
} 