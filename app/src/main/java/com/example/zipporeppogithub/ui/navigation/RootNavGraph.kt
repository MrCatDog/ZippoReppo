package com.example.zipporeppogithub.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zipporeppogithub.ui.SplashScreen

@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Screen.Root.navRoute,
        startDestination = Screen.Splash.navRoute
    ) {
        composable(route = Screen.Splash.navRoute) {
            SplashScreen { navController.navigate(Screen.Home.navRoute) }
        }
        composable(route = Screen.Home.navRoute) {
            HomeScreen()
        }
    }
}