package com.systematicshoppers.sysho.fragments

import android.content.ContentValues
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.adapters.ApiStoresAdapter
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.Store
import java.util.*

/**
 * A fragment of ApiActivity that displays the store database and handles click events from its RecyclerView.
 */
class ApiStoreFragment : Fragment(), ApiStoresAdapter.ClickListener {

    // Declare necessary variables and components
    private lateinit var apiStoresRecyclerView: RecyclerView
    private lateinit var apiStoresAdapter: ApiStoresAdapter
    private val stores = mutableListOf<Store>()
    private lateinit var geocoder: Geocoder
    private val viewModel: SyshoViewModel by activityViewModels()

    /**
     * Inflate the layout for the fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_api_store, container, false)
        return view
    }

    /**
     * Initialize components and get store data from Firebase
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        // Get store data and addresses, then initialize RecyclerView components
        getStores { stores ->
            getAddresses(stores, geocoder) { done -> try {
                if(isAdded) {
                    apiStoresAdapter = ApiStoresAdapter(requireContext(), stores, this)
                    val apiStoresLayoutManager =
                        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    apiStoresRecyclerView = view.findViewById((R.id.api_stores_recycler_view))
                    apiStoresRecyclerView.adapter = apiStoresAdapter
                    apiStoresRecyclerView.layoutManager = apiStoresLayoutManager
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Screen is loading", Toast.LENGTH_SHORT).show()
            }}
        }


    }

    /**
     * Parses Firebase documents into Store objects. Returns as a callback with the store objects.
     */
    private fun getStores(callback: (MutableList<Store>) -> Unit) {
        FirebaseUtils().fireStoreDatabase.collection("stores")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { storeDocument ->
                    Log.d(ContentValues.TAG, "Read document with ID ${storeDocument.id}")
                    stores.add(storeDocument.toObject(Store::class.java))
                }
                callback(stores)
            }
    }

    /**
     * Gets addresses from latitude and longitude values to update store objects with addresses and display them in the UI
     */
    private fun getAddresses(stores: MutableList<Store>, geocoder: Geocoder, callback: (Boolean) -> Unit) {
        var addressList: MutableList<Address>?
        var lat: Double?
        var long: Double?
        for (item in stores.indices) {
            try {
                lat = stores[item].latitude?.toDouble()!!
                long = stores[item].longitude?.toDouble()!!
                val geocodeListener = Geocoder.GeocodeListener { locations ->
                    addressList = locations
                    stores[item].address = addressList!![0].getAddressLine(0).toString()
                }
                geocoder.getFromLocation(lat, long, 1, geocodeListener)
            } catch (e: Exception) {
            }
        }
        callback.invoke(true)
    }

    /**
     * Navigate to ApiStoreSelectFragment when a store item is clicked.
     */
    override fun gotoStore(position: Int, storeData: Store) {
        viewModel.setStoreData(storeData)
        view?.findNavController()?.navigate(R.id.action_apiStoreFragment_to_apiStoreSelectFragment)
    }
}