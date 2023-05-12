package com.example.zipporeppogithub.ui.navigation

import com.example.zipporeppogithub.R

sealed class BottomNavItem(val title: String, val icon: Int, val navDestination: String) {
    object Search :
        BottomNavItem("Search", R.drawable.bottom_nav_search, Screen.NestedGraphRepos.navRoute)

    object History :
        BottomNavItem("History", R.drawable.bottom_nav_download, Screen.NestedGraphHistory.navRoute)
}
