package com.example.zipporeppogithub.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.zipporeppogithub.ui.navigation.RootNavGraph
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //todo Theme
            RootNavGraph(navController = rememberNavController())
        }
    }
}