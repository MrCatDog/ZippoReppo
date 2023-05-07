package com.example.zipporeppogithub.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.ui.navigation.Graph
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    LaunchedEffect(Unit) {
        delay(1000) //todo проверить может это не тут сохранять
        navController.navigate(Graph.HOME)
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.splash_logo_size)),
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "Splash Rabbit Logo"
        )
    }
}