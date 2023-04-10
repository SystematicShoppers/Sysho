package com.systematicshoppers.sysho.fragments

import android.location.Address
import android.location.Geocoder
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.adapters.ApiStoresSelectAdapter
import com.systematicshoppers.sysho.database.Product
import com.systematicshoppers.sysho.database.Store
import java.util.*

class ApiStoreSelectFragment: Fragment(), ApiStoresSelectAdapter.ClickListener {

    private val viewModel: SyshoViewModel by activityViewModels()
    private lateinit var apiStoresSelectAdapter: ApiStoresSelectAdapter
    private lateinit var supportFragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_api_store_select, container, false)
        val storeID = view.findViewById<TextView>(R.id.storeID_at_interface)
        val address = view.findViewById<TextView>(R.id.address_at_interface)
        val storeName = view.findViewById<TextView>(R.id.storeName_at_interface)
        val imageLogo = view.findViewById<ImageView>(R.id.apiStoreLogo)
        val recyclerView = view.findViewById<RecyclerView>(R.id.store_interface_recycler_view)
        val geocoder: Geocoder = Geocoder(requireContext(), Locale.getDefault())
        supportFragmentManager = parentFragmentManager
        viewModel.store.observe(viewLifecycleOwner) {
            val store = viewModel.store.value
            getAddress(store, geocoder)
            if(address != null)
                address.text = store?.address?.getAddressLine(0)
            else {
                val latlongstr = store?.latitude.toString() + " " + store?.longitude.toString()
                address?.text = latlongstr
            }
            storeName.text = store?.store
            storeID.text = store?.storeId
            setLogoImage(storeName.text as String?, imageLogo)
            apiStoresSelectAdapter = ApiStoresSelectAdapter(requireContext(), store?.stock, this)
            val recyclerViewLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
            recyclerView.adapter = apiStoresSelectAdapter
            recyclerView.layoutManager = recyclerViewLayoutManager
        }

            return view
    }

    private fun setLogoImage(store: String?, imageView: ImageView) {
        when (store) {
            "WalMart", "Wal Mart", "Wal-Mart", "Wal - Mart", "Walmart" ->
                imageView.setImageResource(R.drawable.walmart_logo_vector)
            "Aldi", "aldi" ->
                imageView.setImageResource(R.drawable.aldi_logo_vector)
            "Target", "target" ->
                imageView.setImageResource(R.drawable.target_logo_vector)
            else ->
                imageView.setImageResource(android.R.color.transparent)
        }
    }
    private fun getAddress(store: Store?, geocoder: Geocoder) {
        var addressList: MutableList<Address>?
        val lat: Double?
        val long: Double?
        try {
            lat = store?.latitude?.toDouble()!!
            long = store.longitude?.toDouble()!!
            val geocodeListener = Geocoder.GeocodeListener { locations ->
                addressList = locations
                store.address = addressList!![0]
            }
            geocoder.getFromLocation(lat, long, 1, geocodeListener)
        } catch (e: Exception) {
        }
    }

    override fun modifyProduct(position: Int, productData: MutableMap<String, Any>?) {

    }

    override fun updatePrice(position: Int, productData: MutableMap<String, Any>?) {
        val dialog = ApiStoreSelectDialogFragment()
        val product = Product().mapToProduct(productData)
        viewModel.setProductData(product)
        dialog.show(supportFragmentManager, "Price Update")
    }

}