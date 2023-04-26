package com.systematicshoppers.sysho

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * A view model for location services.
 * Updates if there is any change in location caught by the LocationListener.
 */
class LocationViewModel : ViewModel(), LocationListener {
    // Declare variables for location-related information
    private lateinit var locationManager: LocationManager
    val currentLocation = MutableLiveData<Location>()
    val isLocationEnabled = MutableLiveData<Boolean>()
    val isLocationPermissionGranted = MutableLiveData<Boolean>()

    /**
     * startLocationUpdates function initializes the locationManager and starts updating the location if permission is granted.
     * @param context The context of the calling activity.
     */
    fun startLocationUpdates(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check for permission to access location
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            isLocationPermissionGranted.value = false
        } else {
            // Permission granted, start requesting location updates
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                this
            )
            isLocationPermissionGranted.value = true
        }
        // Check if the GPS_PROVIDER is enabled and update the value
        isLocationEnabled.value = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * onLocationChanged function is called when there is a change in the location.
     * @param location The updated location.
     */
    override fun onLocationChanged(location: Location) {
        currentLocation.value = location
    }

    companion object {
        // Define a constant for the location permission request code
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}