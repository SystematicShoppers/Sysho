package com.systematicshoppers.sysho.database

import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.systematicshoppers.sysho.LocationViewModel


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

    fun getAddress(coordinates: Coordinates, geocoder: Geocoder, callback: (String) -> Unit) {
        val lat = coordinates.latitude
        val long = coordinates.longitude

        if (lat != null && long != null) {
            val geocodeListener = Geocoder.GeocodeListener { locations ->
                if (locations.isNotEmpty()) {
                    val address = locations[0]
                    val addressString = address.getAddressLine(0)
                    callback(addressString)
                } else {
                    Log.e(TAG, "No address found for coordinates: $coordinates")
                    callback("") // or pass null to indicate that no address was found
                }
            }
            geocoder.getFromLocation(lat, long, 1, geocodeListener)
        } else {
            Log.e(TAG, "Latitude or longitude were missing from Coordinates. Could not find address.")
            callback("") // or pass null to indicate that no address was found
        }
    }

    fun updateStoreWithStockField(documentId: String) {
        FirebaseUtils().fireStoreDatabase.collection("metadata").document("basePrices")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val stockArray = querySnapshot.get("Stock")
                if(stockArray != null) {
                    FirebaseUtils().fireStoreDatabase.collection("stores").document(documentId)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            // If "Stock" field is not present or not an ArrayList, set it as an empty ArrayList
                            querySnapshot.reference.update("Stock", stockArray)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Stock field added to document ${querySnapshot.id}")
                                }
                                .addOnFailureListener { exception ->
                                    Log.e(TAG, "Failed to update Stock field for document ${querySnapshot.id}: ${exception.message}")
                                }
                        }
                }
            }
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
            return distance[0] / 1609.344 //distance in miles
        }
        else {
            Log.e(TAG, "Distance could not be calculated. Either current location was not provided, or Firebase failed to retrieve coordinates")
            return 0.0
        }
    }
}


