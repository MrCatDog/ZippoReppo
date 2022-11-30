package com.example.zipporeppogithub.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ZippoReppoGitHub) //splash end
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.navView.setupWithNavController(
            binding.navHostFragmentActivityMain.getFragment<NavHostFragment>().navController
        )

        setContentView(binding.root)
    }
}