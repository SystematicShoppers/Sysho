package com.systematicshoppers.sysho.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.adapters.ApiStoresSelectAdapter
import com.systematicshoppers.sysho.database.Product
import com.systematicshoppers.sysho.database.Store
import com.systematicshoppers.sysho.database.TAG
import java.util.*

/**
 * Displays store data from an onclick even on any item from ApiStoreFragment.
 * Gives the user a reload button so they can use the API to update this data directly from this Fragment's view.
 */
class ApiStoreSelectFragment: Fragment(), ApiStoresSelectAdapter.ClickListener {

    // Declare necessary variables and components.
    private val viewModel: SyshoViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var apiStoresSelectAdapter: ApiStoresSelectAdapter
    private lateinit var supportFragmentManager: FragmentManager
    private lateinit var store: Store
    private lateinit var reloadBtn: ImageButton

    /**
     * Inflate the layout for the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_api_store_select, container, false)

        // Initialize necessary components and retrieve store data from ViewModel.
        val storeID = view.findViewById<TextView>(R.id.storeID_at_interface)
        val address = view.findViewById<TextView>(R.id.address_at_interface)
        val storeName = view.findViewById<TextView>(R.id.storeName_at_interface)
        val imageLogo = view.findViewById<ImageView>(R.id.apiStoreLogo)
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        reloadBtn = view.findViewById(R.id.reloadBtn)
        recyclerView = view.findViewById(R.id.store_interface_recycler_view)
        supportFragmentManager = parentFragmentManager

        // Set up the RecyclerView with the store data and update the UI components.
        viewModel.store.observe(viewLifecycleOwner) {
            store = viewModel.store.value!!
            getAddress(store, geocoder)
            val latlongstr = store.latitude.toString() + " " + store.longitude.toString()
            address?.text = latlongstr

            storeName.text = store.store
            storeID.text = store.storeId
            setLogoImage(storeName.text as String?, imageLogo)
            apiStoresSelectAdapter = ApiStoresSelectAdapter(requireContext(), store.stock, this)
            val recyclerViewLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
            recyclerView.adapter = apiStoresSelectAdapter
            recyclerView.layoutManager = recyclerViewLayoutManager
            viewModel.apiStoreAdapterNotice.observe(viewLifecycleOwner) {
                apiStoresSelectAdapter.notifyDataSetChanged()
            }
        }

        // Set up the reload button to update store data and refresh UI components.
        reloadBtn.setOnClickListener {
            viewModel.reloadStoreData(store.DocumentId)
            getAddress(store, geocoder)
            val latlongstr = store.latitude.toString() + " " + store.longitude.toString()
            address?.text = latlongstr
            storeName.text = store.store
            storeID.text = store.storeId
            setLogoImage(storeName.text as String?, imageLogo)
            apiStoresSelectAdapter.notifyDataSetChanged()
        }
        return view
    }

    /**
     * Set the store logo image based on the store name.
     */
    private fun setLogoImage(store: String?, imageView: ImageView) {
        when (store) {
            "WalMart", "Wal Mart", "Wal-Mart", "Wal - Mart", "Walmart" ->
                imageView.setImageResource(R.drawable.walmart_logo)
            "Aldi", "aldi" ->
                imageView.setImageResource(R.drawable.aldi_logo)
            "Target", "target" ->
                imageView.setImageResource(R.drawable.target_logo)
            "Winn Dixie", "winn dixie", "Winn-Dixie", "winn-dixie" ->
                imageView.setImageResource(R.drawable.winn_dixie_logo)
            "Publix", "publix" ->
                imageView.setImageResource(R.drawable.publix_logo)
            else ->
                imageView.setImageResource(android.R.color.transparent)
        }
    }

    /**
     * Get the address from the store's latitude and longitude using the Geocoder.
     */
    private fun getAddress(store: Store?, geocoder: Geocoder) {
        var addressList: MutableList<Address>?
        val lat: Double?
        val long: Double?
        try {
            lat = store?.latitude?.toDouble()!!
            long = store.longitude?.toDouble()!!
            val geocodeListener = Geocoder.GeocodeListener { locations ->
                addressList = locations
                store.address = addressList!![0].getAddressLine(0).toString()
            }
            geocoder.getFromLocation(lat, long, 1, geocodeListener)
        } catch (e: Exception) {
            Log.e(TAG, "Address not found.")
        }
    }

    /**
     * Modify the product. This method is empty because it is not used in this fragment.
     */
    override fun modifyProduct(position: Int, productData: MutableMap<String, Any>?) {

    }

    /**
     * Update the price of a product by showing the ApiStoreDialog when an item is clicked.
     */
    override fun updatePrice(position: Int, productData: MutableMap<String, Any>?) {
        val dialog = ApiStoreDialog()
        val product = Product().mapToProduct(productData)
        viewModel.setProductData(product)
        dialog.show(supportFragmentManager, "Operation Calls")
    }
}