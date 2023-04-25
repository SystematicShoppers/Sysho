package com.systematicshoppers.sysho.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.databinding.ActivityApiBinding

/**
 * ApiActivity is an AppCompatActivity that serves as a container for the fragments
 * involved in API-related operations
 */
class ApiActivity: AppCompatActivity() {
    // Declare variables for the binding and NavController
    private lateinit var binding: ActivityApiBinding
    private lateinit var navController: NavController

    // Function called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the ActivityApiBinding and set the content view to the generated view hierarchy
        val binding = ActivityApiBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        // Retrieve the NavHostFragment and NavController associated with this activity
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.content) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the bottom navigation view with the NavController
        val nav = binding.bottomNavigationView
        nav.setupWithNavController(navController)

        // Set an item selected listener for the bottom navigation view
        nav.setOnItemSelectedListener { item ->
            // Determine the action to perform based on the selected item's ID
            when (item.itemId) {
                R.id.apiStoreFragment -> {
                    // Pop back to the root of the navigation graph, then navigate to the apiStoreFragment
                    navController.popBackStack()
                    navController.navigate(R.id.apiStoreFragment)
                    true
                }
                R.id.apiProductFragment -> {
                    // Pop back to the root of the navigation graph, then navigate to the apiProductFragment
                    navController.popBackStack()
                    navController.navigate(R.id.apiProductFragment)
                    true
                }
                else -> false// Return false for any other unhandled item IDs
            }
        }

    }
}