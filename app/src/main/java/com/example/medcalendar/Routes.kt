package com.example.medcalendar

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
/*
sealed class Routes (val route : String, val args : List<NamedNavArgument> = listOf()){
    data object MainRoute : Routes("mainRoutes"){
        data object Login : Routes("${MainRoute.route}/login"){
            fun NavController.toLogin() = navigate("${MainRoute.route}/login")
        }
        data object Signup : Routes("${MainRoute.route}/signup"){
            fun NavController.toSignup() = navigate("${MainRoute.route}/signup")
        }
        data object Home : Routes("${MainRoute.route}/home"){
            fun NavController.toHome() = navigate("${MainRoute.route}/home")
        }


    }
}*/