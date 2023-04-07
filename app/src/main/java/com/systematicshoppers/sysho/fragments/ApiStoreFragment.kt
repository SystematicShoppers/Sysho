package com.systematicshoppers.sysho.fragments

import android.content.ContentValues
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.adapters.ApiStoresAdapter
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.Store
import java.util.*

class ApiStoreFragment : Fragment(), ApiStoresAdapter.ClickListener {

    private lateinit var apiStoresRecyclerView: RecyclerView
    private lateinit var apiStoresAdapter: ApiStoresAdapter
    private lateinit var geocoder: Geocoder
    private val viewModel: SyshoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_api_store, container, false)
        val stores = mutableListOf<Store>()
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        FirebaseUtils().fireStoreDatabase.collection("stores")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { storeDocument ->
                    Log.d(ContentValues.TAG, "Read document with ID ${storeDocument.id}")
                    stores.add(storeDocument.toObject(Store::class.java))
                }
                getAddresses(stores, geocoder)
                apiStoresAdapter = ApiStoresAdapter(requireContext(), stores, this)
                val apiStoresLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
                apiStoresRecyclerView = view.findViewById((R.id.api_stores_recycler_view))
                apiStoresRecyclerView.adapter = apiStoresAdapter
                apiStoresRecyclerView.layoutManager = apiStoresLayoutManager

            }


        return view
    }

    private fun getAddresses(stores: MutableList<Store>, geocoder: Geocoder) {
        var addressList: MutableList<Address>?
        var lat: Double?
        var long: Double?
        for (item in stores.indices) {
            try {
                lat = stores[item].latitude?.toDouble()!!
                long = stores[item].longitude?.toDouble()!!
                val geocodeListener = Geocoder.GeocodeListener { locations ->
                    addressList = locations
                    stores[item].address = addressList!![0]
                }
                geocoder.getFromLocation(lat, long, 1, geocodeListener)
            } catch (e: Exception) {
            }
        }
    }

    override fun gotoStore(position: Int, storeData: Store) {
        viewModel.setStoreData(storeData)
        view?.findNavController()?.navigate(R.id.action_apiStoreFragment_to_apiStoreSelectFragment)
    }


}