package com.example.finalproject.ui

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val auth = remember { FirebaseAuth.getInstance() }

    fun handleAuthResult(success: Boolean, message: String?) {
        loading = false
        if (success) {
            onAuthSuccess()
        } else {
            error = message ?: "Authentication failed."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(0.85f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                shape = RoundedCornerShape(6.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Black,
                    focusedContainerColor = Color.Black,
                    unfocusedBorderColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                )
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                shape = RoundedCornerShape(6.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Black,
                    focusedContainerColor = Color.Black,
                    unfocusedBorderColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                )
            )
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        // trim & validate
                        val e = email.trim()
                        val p = password.trim()
                        error = null

                        when {
                            e.isEmpty() || p.isEmpty() -> {
                                error = "Email and password cannot be empty"
                            }
                            !Patterns.EMAIL_ADDRESS.matcher(e).matches() -> {
                                error = "Please enter a valid email address"
                            }
                            p.length < 6 -> {
                                error = "Password must be at least 6 characters"
                            }
                            else -> {
                                loading = true
                                auth.createUserWithEmailAndPassword(e, p)
                                    .addOnSuccessListener { handleAuthResult(true, null) }
                                    .addOnFailureListener { handleAuthResult(false, it.localizedMessage) }
                            }
                        }
                    },
                    enabled = !loading
                ) {
                    Text("Register")
                }

                Button(
                    onClick = {
                        val e = email.trim()
                        val p = password.trim()
                        error = null

                        when {
                            e.isEmpty() || p.isEmpty() -> {
                                error = "Email and password cannot be empty"
                            }
                            !Patterns.EMAIL_ADDRESS.matcher(e).matches() -> {
                                error = "Please enter a valid email address"
                            }
                            else -> {
                                loading = true
                                auth.signInWithEmailAndPassword(e, p)
                                    .addOnSuccessListener { handleAuthResult(true, null) }
                                    .addOnFailureListener { handleAuthResult(false, it.localizedMessage) }
                            }
                        }
                    },
                    enabled = !loading
                ) {
                    Text("Login")
                }
            }

            if (loading) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator(color = Color.White)
            }
            error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = Color.Red)
            }
        }
    }
}