package com.example.zipporeppogithub.ui.navigation

import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.zipporeppogithub.ui.history.HistoryScreen
import com.example.zipporeppogithub.ui.history.HistoryViewModel
import com.example.zipporeppogithub.ui.repos.ReposScreen
import com.example.zipporeppogithub.ui.repos.ReposViewModel
import com.example.zipporeppogithub.ui.search.SearchScreen
import com.example.zipporeppogithub.ui.search.SearchViewModel
import com.example.zipporeppogithub.utils.daggerViewModel
import com.example.zipporeppogithub.utils.di.DaggerAppComponent

@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { MainScreenBottomNavigation(navController = navController) }
    ) {
        NavHost(
            navController,
            route = Screen.Home.navRoute,
            startDestination = BottomNavItem.Search.navDestination,
            modifier = Modifier.padding(it)
        ) {
            composable(BottomNavItem.History.navDestination) {
                val context = LocalContext.current
                val viewModel: HistoryViewModel = daggerViewModel {
                    DaggerAppComponent.builder().applicationContext(context).build()
                        .provideHistoryViewModel()
                }
                HistoryScreen(viewModel)
            }

            addDownloadsNavGraph(navController)
        }
    }
}

fun NavGraphBuilder.addDownloadsNavGraph(navController: NavHostController) {
    navigation(
        route = Screen.NestedGraphRepos.navRoute,
        startDestination = Screen.Search.navRoute
    ) {
        composable(route = Screen.Search.navRoute) {
            val context = LocalContext.current
            val viewModel: SearchViewModel = daggerViewModel {
                DaggerAppComponent.builder().applicationContext(context).build()
                    .provideSearchViewModel()
            }
            SearchScreen(viewModel) { user ->
                navController.navigate(Screen.Repos.navRoute + "/" + user.username)//todo args
            }
        }

        composable(route = Screen.Repos.navRoute + "/{userLogin}") {
            val context = LocalContext.current
            val viewModel: ReposViewModel = daggerViewModel {
                DaggerAppComponent.builder().applicationContext(context).build()
                    .provideReposViewModelFactory().create(
                        userLogin = it.arguments!!.getString("userLogin")!!,//todo !!
                        downloadPath = Environment.getExternalStoragePublicDirectory(
                            DIRECTORY_DOWNLOADS
                        ).path
                    )
            }
            ReposScreen(viewModel = viewModel)
        }
    }
}