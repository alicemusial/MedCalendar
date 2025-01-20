package com.example.medcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.medcalendar.presentation.MainViewModel
import com.example.medcalendar.ui.theme.MedCalendarTheme
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medcalendar.auth.LoginPage
import com.example.medcalendar.auth.SignupPage


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedCalendarTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel = hiltViewModel<MainViewModel>()
                    Navigation(viewModel)
                }
            }
        }
    }
}

@Composable
fun Navigation(authViewModel: MainViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){ LoginPage(navController, authViewModel) }
        composable("signup"){ SignupPage(navController, authViewModel) }
        composable("home"){ MainScreen(navController, authViewModel) }
    }
    )
}

