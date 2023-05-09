package com.example.zipporeppogithub.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.ui.navigation.HomeScreen
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ZippoReppoGitHub) //splash end
        super.onCreate(savedInstanceState)

        setContent {
            //todo Theme
            HomeScreen(rememberNavController())
        }
    }
}