package com.systematicshoppers.sysho.activities

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.Product
import com.systematicshoppers.sysho.database.SearchList
import com.systematicshoppers.sysho.database.Store
import com.systematicshoppers.sysho.databinding.ActivityMainBinding
import kotlinx.coroutines.async
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var geocoder: Geocoder
    private lateinit var locationManager: LocationManager
    private val viewModel: SyshoViewModel by viewModels()
    init {

        getAutoCompleteList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        viewModel.autoComplete.observe(this,{
            val autoCompleteList = it
        })
        //Navigation graph (jetpack)
        //The navigation graph has search fragment as home location
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.content) as NavHostFragment
        navController = navHostFragment.navController

        //Bottom navigation
        //Bottom navigation is set to use the navigation graph in nav_menu.xml
        val nav = binding.bottomNavigationView
        nav.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    override fun onStop() {
        super.onStop()
        locationManager.removeUpdates(this)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
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

    override fun onLocationChanged(myLocation: Location) {
        val latitude = myLocation.latitude
        val longitude = myLocation.longitude
        viewModel.setCurrentLat(latitude)
        viewModel.setCurrentLong(longitude)
    }
}