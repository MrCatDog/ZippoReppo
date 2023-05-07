package com.example.zipporeppogithub.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zipporeppogithub.ui.history.HistoryScreen
import com.example.zipporeppogithub.ui.history.HistoryViewModel
import com.example.zipporeppogithub.ui.search.SearchScreen
import com.example.zipporeppogithub.ui.search.SearchViewModel
import com.example.zipporeppogithub.utils.daggerViewModel
import com.example.zipporeppogithub.utils.di.DaggerAppComponent

@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { MainScreenBottomNavigation(navController = navController) }
    ) {
        NavHost(
            navController,
            startDestination = BottomNavItem.Search.navDestination,
            modifier = Modifier.padding(it)
        ) {
            composable(BottomNavItem.Search.navDestination) {
                val context = LocalContext.current
                val viewModel: SearchViewModel = daggerViewModel {
                    DaggerAppComponent.builder().applicationContext(context).build()
                        .provideSearchViewModel()
                }
                SearchScreen(viewModel)
            }

            composable(BottomNavItem.History.navDestination) {
                val context = LocalContext.current
                val viewModel: HistoryViewModel = daggerViewModel {
                    DaggerAppComponent.builder().applicationContext(context).build()
                        .provideHistoryViewModel()
                }
                HistoryScreen(viewModel)
            }
        }
    }
}