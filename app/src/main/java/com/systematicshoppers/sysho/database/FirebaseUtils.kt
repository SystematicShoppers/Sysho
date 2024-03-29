package com.systematicshoppers.sysho.database

import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.systematicshoppers.sysho.LocationViewModel

/**
 * Provides an instance of firebase for the app
 * Provides functions that retrieve information about stores based on coordinates
 * FirebaseLocationsUtil is a utility class specifically for location services.
 */
class FirebaseUtils  {
    val fireStoreDatabase = FirebaseFirestore.getInstance()

    /**
     * Get the address string from the provided coordinates using the Geocoder.
     * Callback function is used to return the address string asynchronously.
     */
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

    /**
     * This is a function for resetting a store to the default prices and products database.
     * Used to assist in development.
     */
    fun updateStoreWithStockField(documentId: String) {
        FirebaseUtils().fireStoreDatabase.collection("metadata").document("basePrices")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val stockArray = querySnapshot.get("Stock")
                if(stockArray != null) {
                    FirebaseUtils().fireStoreDatabase.collection("stores").document(documentId)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            // If "Stock" field is not present or not an ArrayList, set it as an empty ArrayList
                            documentSnapshot.reference.update("Stock", stockArray)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Stock field added to document ${documentSnapshot.id}")
                                }
                                .addOnFailureListener { exception ->
                                    Log.e(TAG, "Failed to update Stock field for document ${documentSnapshot.id}: ${exception.message}")
                                }
                        }
                }
            }
    }
}

/**
 * Utility class designed to work with location-related data within the context of the fragment.
 * Primarily focuses on calculating the distance between two sets of coordinates using the current location of the device
 * and coordinates retrieved from the Firebase database.
 */
class FirebaseLocationUtils(private val activity: FragmentActivity) {
    private val locationViewModel: LocationViewModel by lazy {
        ViewModelProvider(activity)[LocationViewModel::class.java]
    }

    /**
     * Get the distance between the current location and the provided coordinates.
     * Returns the distance in miles.
     */
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