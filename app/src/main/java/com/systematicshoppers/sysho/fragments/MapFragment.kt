package com.systematicshoppers.sysho.fragments

import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import com.systematicshoppers.sysho.BuildConfig.MAPS_API_KEY
import com.systematicshoppers.sysho.LocationViewModel
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.database.Coordinates
import com.systematicshoppers.sysho.database.FirebaseUtils
import java.util.*
import kotlin.collections.ArrayList

class MapFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private var destinationLongitude: Double = 0.0
    private var destinationLatitude: Double = 0.0
    private var address: String = ""
    private val locationViewModel: LocationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val address = arguments?.getString("address") //this is the position for the item in ResultsFragment recyclerview... may not be necessary
        val coordinates = arguments?.getParcelable("coordinates", Coordinates::class.java)
        destinationLongitude = coordinates?.longitude!!
        destinationLatitude = coordinates.latitude!!
        val mapView = view.findViewById<MapView>(R.id.map_container)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { googleMap ->

            val origin = locationViewModel.currentLocation.value
            val location = LatLng(destinationLatitude, destinationLongitude)
            var destination = ""
            FirebaseUtils().getAddress(coordinates, geocoder = Geocoder(requireContext(), Locale.getDefault())) { address ->
                destination = address
            }

            mMap = googleMap
            mMap.addMarker(MarkerOptions().position(location))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

            /** DirectionsAPI call **/

            // Initialize the API context
            val context = GeoApiContext.Builder()
                .apiKey(MAPS_API_KEY)
                .build()

            // Make the Directions API request
            val request = DirectionsApi.newRequest(context)
                .origin(address)
                .destination(destination)
            val result: DirectionsResult = request.await()

            // Loop through the legs and steps to get the polyline information
            val points = ArrayList<LatLng>()
            for (leg in result.routes[0].legs) {
                for (step in leg.steps) {
                    val polyline = step.polyline
                    val decoded = PolylineEncoding.decode(polyline.encodedPath)
                    for (latLng in decoded) {
                        points.add(LatLng(step.startLocation.lat, step.startLocation.lng))
                    }
                }
            }

            // Add the polyline to the map
            val polylineOptions = PolylineOptions().addAll(points)
            mMap.addPolyline(polylineOptions)

            // Move the camera to show the entire route
            val boundsBuilder = LatLngBounds.builder()
            for (point in points) {
                boundsBuilder.include(point)
            }
            val bounds = boundsBuilder.build()
            val padding = resources.getDimensionPixelSize(R.dimen.map_inset_padding)
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap.moveCamera(cameraUpdate)
        }

        return view
    }
}

