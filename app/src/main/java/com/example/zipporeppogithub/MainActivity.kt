package com.example.zipporeppogithub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //todo перенести во фрагмент?
        setTheme(R.style.Theme_ZippoReppoGitHub) //splash end
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}