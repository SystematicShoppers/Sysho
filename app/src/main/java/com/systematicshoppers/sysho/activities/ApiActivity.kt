package com.systematicshoppers.sysho.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.databinding.ActivityApiBinding
import com.systematicshoppers.sysho.databinding.ActivityMainBinding

class ApiActivity: AppCompatActivity() {
    private lateinit var binding: ActivityApiBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        //Navigation graph (jetpack)
        //The navigation graph has search fragment as home location
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.content) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        //Bottom navigation
        //Bottom navigation is set to use the navigation graph in nav_menu.xml
        val nav = binding.bottomNavigationView
        nav.setupWithNavController(navController)
    }
}