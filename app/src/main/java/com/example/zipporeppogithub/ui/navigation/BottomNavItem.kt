package com.example.zipporeppogithub.ui.navigation

import com.example.zipporeppogithub.R

sealed class BottomNavItem(val title: Int, val icon: Int, val navDestination: String) {
    object Search :
        BottomNavItem(
            R.string.Search,
            R.drawable.bottom_nav_search,
            Screen.NestedGraphRepos.navRoute
        )

    object History :
        BottomNavItem(
            R.string.History,
            R.drawable.bottom_nav_download,
            Screen.NestedGraphHistory.navRoute
        )
}
