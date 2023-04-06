package com.systematicshoppers.sysho.database

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.systematicshoppers.sysho.LocationViewModel
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CompletableFuture

/**
 *
 * Provides an instance of firebase for the app
 * Provides functions that retrieve information about stores based on coordinates
 * FirebaseLocationsUtil is a utility class specifically for location services.
 *
 * **/
class FirebaseUtils  {
    val fireStoreDatabase = FirebaseFirestore.getInstance()
    fun getStoreName(coordinates: Coordinates, callback: (String) -> Unit) {
        val latitude: String = coordinates.latitude.toString()
        val longitude: String = coordinates.longitude.toString()
        val collectionRef = fireStoreDatabase.collection("stores")
        collectionRef
            .whereEqualTo("Latitude", latitude)
            .whereEqualTo("Longitude", longitude)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val storeName = document.getString("Store").toString()
                    if (storeName.isNotEmpty()) {
                        callback(storeName)
                        return@addOnSuccessListener // exit the function after the first match
                    }
                }
            }
            .addOnFailureListener { exception ->
                //TODO: Add error handling
            }
    }

    fun getAddress(coordinates: Coordinates, geocoder: Geocoder): String {
        var addressString = ""
        val lat = coordinates.latitude
        val long = coordinates.longitude
        var address: Address? = null

        try {
            val geocodeListener = Geocoder.GeocodeListener { locations ->
                address = locations[0]
            }
            if (lat != null && long != null) {
                geocoder.getFromLocation(lat, long, 1, geocodeListener)
                addressString = address?.getAddressLine(0).toString()
            }
            else
                Log.e(TAG, "Latitude or longitude were missing from Coordinates. Could not find address.")
        } catch (e: Exception) {
            Log.e(TAG, "Geocoder failed to call getLocation. Requires SDK 33 or higher")
        }
        return addressString
    }

}

class FirebaseLocationUtils(private val activity: FragmentActivity) {
    val fireStoreDatabase = FirebaseFirestore.getInstance()
    private val locationViewModel: LocationViewModel by lazy {
        ViewModelProvider(activity)[LocationViewModel::class.java]
    }
    fun getDistance(coordinates: Coordinates, geocoder: Geocoder): Double {
        val currentLocation = locationViewModel.currentLocation.value
        val distance = FloatArray(1)

        if (currentLocation != null && coordinates.latitude != null && coordinates.longitude != null) {
            Location.distanceBetween(
                currentLocation.latitude, currentLocation.longitude,
                coordinates.latitude!!, coordinates.longitude!!,
                distance
            )
        }
        else {
            Log.e(TAG, "Distance could not be calculated. Either current location was not provided, or Firebase failed to retrieve coordinates")
            return 0.0
        }
        return distance[0].toDouble()
    }
}


