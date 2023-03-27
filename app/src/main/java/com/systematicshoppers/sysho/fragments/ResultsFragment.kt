
package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.adapters.StoreElementAdapter

class ResultsFragment : Fragment(), StoreElementAdapter.ClickListener {

    private val viewModel: SyshoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_results, container, false)
        val mapFragment = MapFragment()
        val supportFragmentManager = parentFragmentManager
        val list = viewModel.resultsList.value


        if (list != null) {
            viewModel.setTotalPrice(viewModel.getTotalPrice(list))
            // Currently the total price is based off of the product database.
            // Change to individual store database in the future.

        }

        val mapView = view.findViewById<MapView>(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { googleMap ->
            val gainesville = LatLng(29.6516, -82.3248)
            googleMap.addMarker(MarkerOptions().position(gainesville).title("Gainesville, Florida"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gainesville, 10f))
            // var lat = viewModel.currentLat.value ?: 23.0
            // var long = viewModel.currentLong.value ?: -83.0
            // val markerOptions = MarkerOptions()
            // val centerLatLng = LatLng(lat, long)
            // markerOptions.title("My Location")
            // markerOptions.position(centerLatLng)
            // googleMap.addMarker(markerOptions)
            //FirebaseUtils().fireStoreDatabase.collection("stores")
            //    .get()
            //    .addOnSuccessListener {
            //       //TODO("Get a list of all lat and long pairs for all stores")
            //    }
        }

        return view
    }

    override fun locationIntent() {
        //TODO("Not yet implemented")
    }

}