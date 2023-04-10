package com.systematicshoppers.sysho.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.databinding.ActivityApiBinding
import com.systematicshoppers.sysho.databinding.ActivityMainBinding
import com.systematicshoppers.sysho.fragments.ApiProductFragment
import com.systematicshoppers.sysho.fragments.ApiStoreFragment

class ApiActivity: AppCompatActivity() {
    private lateinit var binding: ActivityApiBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityApiBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.content) as NavHostFragment
        navController = navHostFragment.navController

        val nav = binding.bottomNavigationView
        nav.setupWithNavController(navController)

        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.apiStoreFragment -> {
                    navController.popBackStack()
                    navController.navigate(R.id.apiStoreFragment)
                    true
                }
                R.id.apiProductFragment -> {
                    navController.popBackStack()
                    navController.navigate(R.id.apiProductFragment)
                    true
                }
                else -> false
            }
        }

    }
}