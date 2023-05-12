package com.example.zipporeppogithub.ui.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.zipporeppogithub.R

@Composable
fun MainScreenBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Search,
        BottomNavItem.History
    )
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { item ->
            val title = stringResource(id = item.title)
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = title) },
                label = {
                    Text(
                        text = title,
                        fontSize = dimensionResource(id = R.dimen.bottom_nav_item_font_size).value.sp
                    )
                },
                alwaysShowLabel = true,
                selected = currentDestination?.hierarchy?.any { it.route == item.navDestination } == true,
                onClick = {
                    navController.navigate(item.navDestination) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
