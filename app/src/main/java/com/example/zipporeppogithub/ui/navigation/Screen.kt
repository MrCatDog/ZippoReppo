package com.example.zipporeppogithub.ui.navigation

sealed class Screen(val navRoute: String) {
    object NestedGraphRepos : Screen("nested_repos")
    object Search : Screen("search")
    object Repos : Screen("repos")


    object NestedGraphHistory : Screen("nested_history")
    object History : Screen("history")
}
