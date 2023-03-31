
package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.util.Log
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
import com.systematicshoppers.sysho.database.Coordinates
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.Product
import com.systematicshoppers.sysho.database.TAG

class ResultsFragment : Fragment(), StoreElementAdapter.ClickListener {

    private val viewModel: SyshoViewModel by activityViewModels()
    private lateinit var firebaseCoordinates: MutableList<Coordinates>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_results, container, false)
        val list = viewModel.resultsList.value
        val mapView = view.findViewById<MapView>(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { googleMap ->
            val gainesville = LatLng(29.6516, -82.3248)
            googleMap.addMarker(MarkerOptions().position(gainesville).title("Gainesville, Florida"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gainesville, 10f))
            loadCoordinates()
            if (list != null) {
                getTotalPrice(list)
            }
            viewModel.loadCoordinatesCallback.observe(viewLifecycleOwner) {
                // TODO: Add all map logic code here. This code will run after the map and firebase data. have been loaded
                println("Coordinates have been loaded!")
                viewModel.totalPriceCallback.observe(viewLifecycleOwner) {
                    // Currently the total price is based off of the product database.
                    // Change to individual store database in the future.
                    // TODO: add adapters for recyclerView here, in the totalPriceCallback observer, after all firebase data is present.
                    if(viewModel.totalPriceCallback.value == true)
                        println("List total price has been calculated from firebase prices!")
                    println("List total = $${viewModel.totalPrice.value}")
                }
            }
        }
        return view
    }

    override fun locationIntent() {
        //TODO("Not yet implemented")
    }


    private fun getTotalPrice(items: List<String>) {
        var total = 0.0
        for(i in items.indices) {
            try {
                FirebaseUtils().fireStoreDatabase.collection("products").document(items[i])
                    .get()
                    .addOnSuccessListener { document ->
                        val product = document.toObject(Product::class.java)
                        val itemPrice = product?.price?.toDouble()
                        if (itemPrice != null)
                            total += itemPrice
                        if (i == items.lastIndex)
                            totalPriceLoaded(true, total)
                    }
            } catch(e: Exception) {
                Log.v(TAG, "Could not retrieve item on list from Firebase. Price of ${items[i]} was not included in total.")
                totalPriceLoaded(false, 0.0)
            }
        }
    }

    private fun loadCoordinates() {
        firebaseCoordinates = mutableListOf()
        FirebaseUtils().fireStoreDatabase.collection("stores")
            .get()
            .addOnSuccessListener { querySnapshot ->
                var lat: String
                var long: String
                var coordinate: Coordinates
                querySnapshot.forEach { document ->
                    lat = document.get("Latitude").toString()
                    long = document.get("Longitude").toString()
                    if(lat != "null" && long != "null") {
                        coordinate = Coordinates(lat.toDouble(), long.toDouble())
                        firebaseCoordinates.add(coordinate)
                    }
                }
                coordinatesLoaded()
            }
    }

    private fun coordinatesLoaded() {
        viewModel.loadCoordinatesCallback(true)
    }

    private fun totalPriceLoaded(result: Boolean, total: Double) {
        viewModel.setTotalPrice(total)
        viewModel.totalPriceCallback(result, total)
    }

}