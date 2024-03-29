package com.systematicshoppers.sysho.fragments

import android.content.ContentValues
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.LocationViewModel
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.adapters.ResultsAdapter
import com.systematicshoppers.sysho.database.*
import java.util.*

/**
 * The main function of the app and a fragment of MainActivity. ResultsFragment displays the search results from SearchFragment. It also
 * parses multiple Firebase data points through getTotalPrice(), getAddresses(), and getDistances(). Displays error messages for empty lists,
 * searching without location services, or empty search results due to distance constraints in the settings.
 */
class ResultsFragment : Fragment(), ResultsAdapter.ClickListener {

    private val viewModel: SyshoViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private var list: List<ListItem>? = listOf()
    private lateinit var data: MutableList<Store>
    private lateinit var toggleDistance: ToggleButton
    private lateinit var togglePrice: ToggleButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var resultsAdapter: ResultsAdapter
    private lateinit var recyclerViewLayoutManager: LinearLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_results, container, false)
        val resultsEmptyListButton = view.findViewById<Button>(R.id.resultsEmptyListButton)
        data = mutableListOf()
        list = viewModel.resultsList.value
        toggleDistance = view.findViewById(R.id.filterDistance)
        togglePrice = view.findViewById(R.id.filterPrice)

        /**Checks for location services then attempts to perform a series of callback functions on Firebase.**/
        if (locationViewModel.isLocationEnabled.value == true &&
            locationViewModel.isLocationPermissionGranted.value == true
        ) {
            try {
                if (list?.isNotEmpty()!!) {
                    loadStores { storesLoaded ->
                        getTotalPrice { pricesLockedIn, total ->
                            getAddresses()
                            getDistances()
                            /**Error block for a search with no results due to distance filters.**/
                            if(data.size < 1) {
                                val errorDistanceLayout: LinearLayout =
                                    view.findViewById(R.id.distanceErrorDisplay)
                                errorDistanceLayout.visibility = View.VISIBLE
                                toggleDistance.visibility = View.INVISIBLE
                                togglePrice.visibility = View.INVISIBLE
                                view.findViewById<TextView>(R.id.filterTextView).visibility = View.INVISIBLE
                            }
                            resultsAdapter = ResultsAdapter(requireContext(), data, this)
                            /**Because the callbacks cannot ensure when the fragment attaches to the activity, isAdded will check. Prevents async crashes**/
                            if(isAdded)
                                recyclerViewLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                            recyclerView = view.findViewById(R.id.resultsRecyclerView)
                            recyclerView.adapter = resultsAdapter
                            recyclerView.layoutManager = recyclerViewLayoutManager

                            //the following sets the default filter toggle to "distance".
                            toggleDistance.setBackgroundResource(R.drawable.toggle_left)
                            togglePrice.setBackgroundResource(R.drawable.toggle_off_right)
                            toggleDistance.isChecked = true
                            togglePrice.isChecked = false
                            data.sortBy { it.distance }
                            resultsAdapter.notifyDataSetChanged()
                        }
                    }
                }
                /**Code runs here if there is an empty list **/
                else {
                    val errorEmptyListLayout: LinearLayout =
                        view.findViewById(R.id.resultsEmptyListErrorDisplay)
                    errorEmptyListLayout.visibility = View.VISIBLE
                    resultsEmptyListButton.visibility = View.VISIBLE
                    toggleDistance.visibility = View.INVISIBLE
                    togglePrice.visibility = View.INVISIBLE
                    view.findViewById<TextView>(R.id.filterTextView).visibility = View.INVISIBLE
                    resultsEmptyListButton.setOnClickListener{
                        val newFragment = SearchFragment()
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.content, newFragment).commit()
                    }
                }
            /**Displays the empty list error.**/
            } catch (e: Exception) {
                val errorEmptyListLayout: LinearLayout =
                    view.findViewById(R.id.resultsEmptyListErrorDisplay)
                errorEmptyListLayout.visibility = View.VISIBLE
                resultsEmptyListButton.visibility = View.VISIBLE
                toggleDistance.visibility = View.INVISIBLE
                togglePrice.visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.filterTextView).visibility = View.INVISIBLE
                resultsEmptyListButton.setOnClickListener{
                    val newFragment = SearchFragment()
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.content, newFragment).commit()
                }
            }
        }

        /**Displays no location services error.**/
        if(locationViewModel.isLocationEnabled.value != true ||
            locationViewModel.isLocationPermissionGranted.value != true) {
            val errorNoLocationLayout: LinearLayout =
                view.findViewById(R.id.resultsNoLocationErrorDisplay)
            errorNoLocationLayout.visibility = View.VISIBLE
            toggleDistance.visibility = View.INVISIBLE
            togglePrice.visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.filterTextView).visibility = View.INVISIBLE
        }

        /**Filter toggle UI**/
        toggleDistance.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                toggleDistance.setBackgroundResource(R.drawable.toggle_left)
                togglePrice.setBackgroundResource(R.drawable.toggle_off_right)
                togglePrice.isChecked = false
                data.sortBy { it.distance }
                resultsAdapter.notifyDataSetChanged()
            } else {
                toggleDistance.setBackgroundResource(R.drawable.toggle_off_left)
                toggleDistance.isChecked = false
                resultsAdapter.notifyDataSetChanged()
            }
        }

        togglePrice.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                togglePrice.setBackgroundResource(R.drawable.toggle_right)
                toggleDistance.setBackgroundResource(R.drawable.toggle_off_left)
                toggleDistance.isChecked = false
                data.sortBy { it.totalPrice }
            } else {
                togglePrice.setBackgroundResource(R.drawable.toggle_off_right)
                togglePrice.isChecked = false
                resultsAdapter.notifyDataSetChanged()
            }
        }

        // Overload the back button press to reset the view to the search fragment
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val newFragment = SearchFragment()
                val transaction = parentFragmentManager.beginTransaction()
                viewModel.setCurrentFragment("SearchFragment").toString()
                transaction.replace(R.id.content, newFragment).commit()
            }
        }
        // Add the callback to the onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return view
    }

    /**
     * This takes the quantity modifier from the SearchList object and gets a total price based on Firebase price data.
     * Returns as a callback with the total.
     */
    private fun getTotalPrice(callback: (Boolean, Double) -> Unit) {
        var total = 0.0
        for(store in data) {
            for(stock in store.stock!!) {
                for(listItem in list?.indices!!) {
                    val productName = stock["ProductName"] as? String
                    if (list!![listItem].entry == productName) {
                        val priceString = stock["Price"].toString()
                        var price = priceString.toDouble()
                        price *= list!![listItem].quantity
                        total += price
                    }
                }
            }
            store.totalPrice = total
            total = 0.0
        }
        callback(true, total)
    }

    /**
     * Prevents Firebase from parsing data that is outside the distance filter.
     */
    fun filterDistance(coordinates: Coordinates): Boolean {
        val distance = FirebaseLocationUtils(this.requireActivity()).getDistance(coordinates, geocoder = Geocoder(requireContext(), Locale.getDefault()))
        val maxDistance = viewModel.distanceFilter.value
        return distance < maxDistance!!
    }

    /**
     * Opens Google Maps with the specified address and coordinates.
     */
    override fun gotoMap(address: String, coordinates: Coordinates) {
        val origin = locationViewModel.currentLocation.value
        val geoUri = "google.navigation:q=$address&dirflg=d&origin=${origin?.latitude},${origin?.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }


    /**
     * This method loads coordinates data from Firebase and then filters out any results
     * outside of the settings distance filter
     */
    private fun loadStores(callback: (Boolean) -> Unit) {
        var lat: String
        var long: String
        var coordinates: Coordinates
        FirebaseUtils().fireStoreDatabase.collection("stores")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { storeDocument ->
                    lat = storeDocument.get("Latitude").toString()
                    long = storeDocument.get("Longitude").toString()
                    Log.d(ContentValues.TAG, "Read document with ID ${storeDocument.id}")
                    if(lat != "null" && long != "null") {
                        coordinates = Coordinates(lat.toDouble(), long.toDouble())
                        if(filterDistance(coordinates))
                            data.add(storeDocument.toObject(Store::class.java))
                    }
                }
                callback.invoke(true)
            }
    }

    /**
     * Retrieves the addresses of the stores using their coordinates.
     */
    private fun getAddresses() {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var lat: String
        var long: String
        var coordinates: Coordinates
        for (store in data.indices) {
            lat = data[store].latitude.toString()
            long = data[store].longitude.toString()
            if(lat != "null" && long != "null") {
                coordinates = Coordinates(lat.toDouble(), long.toDouble())
                FirebaseUtils().getAddress(coordinates,geocoder) { address ->
                    data[store].address = address
                }
            }
        }
    }

    /**
     * Retrieves the distances of the stores from the current location.
     */
    private fun getDistances() {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var lat: String
        var long: String
        var coordinates: Coordinates
        for (store in data.indices) {
            lat = data[store].latitude.toString()
            long = data[store].longitude.toString()
            if(lat != "null" && long != "null") {
                coordinates = Coordinates(lat.toDouble(), long.toDouble())
                data[store].distance = FirebaseLocationUtils(requireActivity()).getDistance(coordinates, geocoder)
            }
        }
    }
}