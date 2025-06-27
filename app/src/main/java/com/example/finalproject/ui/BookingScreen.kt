package com.example.finalproject.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.finalproject.R
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    providerId: String,
    onSubmitBooking: (providerId: String, date: LocalDate, slot: String) -> Unit
) {
    // 1️⃣ State
    var pickedDate by remember { mutableStateOf<LocalDate?>(null) }
    var pickedSlot by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // 2️⃣ DatePickerDialog
    val today = Calendar.getInstance()
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                pickedDate = LocalDate.of(year, month + 1, dayOfMonth)
                pickedSlot = null
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1  // can't pick past
        }
    }

    // 3️⃣ Dummy availability: replace with your real data source
    fun availableSlotsFor(date: LocalDate): List<String> = listOf(
        "09:00", "10:30", "12:00", "14:00", "16:30"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(stringResource(R.string.booking_title))
            })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.booking_for, providerId),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(24.dp))

            // 📅 Select Date
            OutlinedButton(
                onClick = { datePicker.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = pickedDate?.toString() ?: stringResource(R.string.select_date),
                    color = if (pickedDate != null)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))

            // ⏰ Show slots once date is chosen
            pickedDate?.let { date ->
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availableSlotsFor(date)) { slot ->
                        FilterChip(
                            selected = (slot == pickedSlot),
                            onClick = { pickedSlot = slot },
                            label = { Text(slot) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ▶️ Submit Booking
            Button(
                onClick = {
                    // safe unwrap: enabled only when both are non-null
                    onSubmitBooking(providerId, pickedDate!!, pickedSlot!!)
                },
                enabled = pickedDate != null && pickedSlot != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.submit_booking))
            }
        }
    }
}