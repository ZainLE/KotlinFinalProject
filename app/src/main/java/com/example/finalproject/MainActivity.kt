package com.example.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.example.finalproject.data.ReviewsRepository
import com.example.finalproject.data.ProviderDetailsViewModel
import com.example.finalproject.ui.ProviderDetailsViewModelFactory
import com.example.finalproject.ui.AuthScreen
import com.example.finalproject.ui.LandingPage
import com.example.finalproject.ui.ProviderDetailsScreen
import com.example.finalproject.ui.BookingScreen
import com.example.finalproject.ui.theme.FinalProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FinalProjectTheme {
                var isAuthenticated by remember {
                    mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
                }

                if (isAuthenticated) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "landing"
                    ) {
                        composable("landing") {
                            LandingPage(navController = navController)
                        }

                        composable(
                            route = "provider/{id}",
                            arguments = listOf(navArgument("id") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val providerId = backStackEntry.arguments
                                ?.getString("id")
                                .orEmpty()

                            val app = applicationContext as FinalProjectApp
                            val dao = app.database.reviewDao()
                            val repo = ReviewsRepository(dao)

                            // use the factory so the ViewModel gets both params
                            val viewModel: ProviderDetailsViewModel = viewModel(
                                factory = ProviderDetailsViewModelFactory(repo, providerId)
                            )

                            ProviderDetailsScreen(
                                navController = navController,
                                providerId = providerId,
                                viewModel = viewModel
                            )
                        }

                        composable(
                            route = "booking/{providerId}",
                            arguments = listOf(navArgument("providerId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val providerId = backStackEntry.arguments
                                ?.getString("providerId")
                                .orEmpty()

                            BookingScreen(
                                providerId = providerId,
                                onSubmitBooking = { id, date, slot ->
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                } else {
                    AuthScreen(onAuthSuccess = { isAuthenticated = true })
                }
            }
        }
    }
}