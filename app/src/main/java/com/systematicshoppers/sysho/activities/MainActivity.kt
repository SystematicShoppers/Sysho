package com.systematicshoppers.sysho.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.databinding.ActivityMainBinding
import com.systematicshoppers.sysho.fragments.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: SyshoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //FireBase
        val db = Firebase.firestore

        //View binding
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}