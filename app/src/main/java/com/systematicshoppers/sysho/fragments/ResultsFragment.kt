
package com.systematicshoppers.sysho.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.LocationViewModel
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.adapters.ResultsAdapter
import com.systematicshoppers.sysho.database.Coordinates
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.Product
import com.systematicshoppers.sysho.database.TAG

class ResultsFragment : Fragment(), ResultsAdapter.ClickListener {

    private val viewModel: SyshoViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private lateinit var firebaseCoordinates: MutableList<Coordinates>
    private lateinit var recyclerView: RecyclerView
    private lateinit var resultsAdapter: ResultsAdapter
    private lateinit var recyclerViewLayoutManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_results, container, false)
        val list = viewModel.resultsList.value

        loadCoordinates()
        if (list != null) {
            getTotalPrice(list)
        }
            viewModel.loadCoordinatesCallback.observe(viewLifecycleOwner) {
                println("Coordinates have been loaded!")
                viewModel.totalPriceCallback.observe(viewLifecycleOwner) {
                    // Currently the total price is based off of the product database.
                    // Change to individual store database in the future.
                    resultsAdapter = ResultsAdapter(requireContext(), requireActivity(), firebaseCoordinates, this)
                    recyclerViewLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
                    recyclerView = view.findViewById(R.id.resultsRecyclerView)
                    recyclerView.adapter = resultsAdapter
                    recyclerView.layoutManager = recyclerViewLayoutManager
                    if(viewModel.totalPriceCallback.value == true)
                        println("List total price has been calculated from firebase prices!")
                    println("List total = $${viewModel.totalPrice.value}")
                }
            }
        return view
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

    override fun gotoMap(address: String, coordinates: Coordinates) {
        val origin = locationViewModel.currentLocation.value
        val geoUri = "google.navigation:q=$address&dirflg=d&origin=${origin?.latitude},${origin?.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }


}