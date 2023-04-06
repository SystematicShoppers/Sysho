package com.systematicshoppers.sysho.activities

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.systematicshoppers.sysho.LocationViewModel
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.SearchList
import com.systematicshoppers.sysho.database.Store
import com.systematicshoppers.sysho.databinding.ActivityMainBinding
import com.systematicshoppers.sysho.fragments.ResultsFragment
import com.systematicshoppers.sysho.fragments.SavedListsFragment
import com.systematicshoppers.sysho.fragments.SearchFragment
import com.systematicshoppers.sysho.fragments.SettingsFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var geocoder: Geocoder
    private lateinit var currentFragmentName: String
    private val viewModel: SyshoViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()
    init {
        getAutoCompleteList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        val toolbar = binding.toolbar
        setContentView(viewRoot)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
       // viewModel.autoComplete.observe(this,{
       //     val autoCompleteList = it
       // })
        locationViewModel.startLocationUpdates(this)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.content) as NavHostFragment
        navController = navHostFragment.navController
        currentFragmentName = navController.currentDestination.toString()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val frame = R.id.content
        if (currentFragmentName.isEmpty()) {
            val currentDestination = navController.currentDestination
            currentFragmentName = currentDestination?.label.toString()
        }
        val fragmentManager = supportFragmentManager
        return when (item.itemId) {
            R.id.searchFragment -> {
                if(currentFragmentName != "SearchFragment") {
                    val newFragment = SearchFragment()
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(frame, newFragment).commit()
                    currentFragmentName = "SearchFragment"
                    true
                } else true
            }
            R.id.resultsFragment -> {
                if(currentFragmentName != "ResultsFragment") {
                    val newFragment = ResultsFragment()
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(frame, newFragment).commit()
                    currentFragmentName = "ResultsFragment"
                    true
                } else true
            }
            R.id.settingsFragment -> {
                if(currentFragmentName != "SettingsFragment") {
                    val newFragment = SettingsFragment()
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(frame, newFragment).commit()
                    currentFragmentName = "SettingsFragment"
                    true
                } else true
            }
            R.id.logInActivity -> {
                val intent = Intent(this, LogInActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.savedListsFragment -> {
                if(currentFragmentName != "SavedListsFragment") {
                    val newFragment = SavedListsFragment()
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(frame, newFragment).commit()
                    currentFragmentName = "SavedListsFragment"
                    true
                } else true
            }
            // add more cases for each menu item
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getAddresses(geocoder: Geocoder) {
        var addressList: MutableList<Address>?
        var address: Address
        var store: Store
        var lat: Double
        var long: Double

        FirebaseUtils().fireStoreDatabase.collection("stores")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { storeDocument ->
                    store = storeDocument.toObject(Store::class.java)
                    lat = store.latitude?.toDouble()!!
                    long = store.longitude?.toDouble()!!
                    if (Build.VERSION.SDK_INT >= 33) {
                        val geocodeListener = Geocoder.GeocodeListener { locations ->
                            addressList = locations
                            address = addressList!![0]

                        }
                        geocoder.getFromLocation(lat, long, 1, geocodeListener)
                    } else {
                        val addressList = geocoder.getFromLocation(lat, long, 1)
                        address = addressList!![0]
                        // For Android SDK < 33, the addresses list will be still obtained from the getFromLocation() method
                    }
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    private fun getAutoCompleteList() {
            FirebaseUtils().fireStoreDatabase.collection("metadata").document("searchList")
                .get()
                .addOnSuccessListener { list ->
                    try {
                        Log.d(ContentValues.TAG, "Data retrieved: ${list.data}")
                        val listObject: SearchList? = list.toObject(SearchList::class.java)
                        viewModel.setAutoComplete(listObject?.searchList!!)

                    } catch (e: Exception) {
                        Log.d(
                            ContentValues.TAG,
                            "Could not retrieve metadata searchList ${list.id}"
                        )
                    }
                }
    }

}