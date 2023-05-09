package com.example.zipporeppogithub.ui.navigation

sealed class Screen(val navRoute: String) {
    object Root : Screen("root_graph")
    object Splash : Screen("splash")

    object Home : Screen("home_graph")
    object Search : Screen("search")
    object History : Screen("history")

    object NestedGraphRepos: Screen("nested_repos")
    object Repos : Screen("repos")
}