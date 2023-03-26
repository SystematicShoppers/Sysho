
package com.systematicshoppers.sysho.fragments

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.adapters.StoreElementAdapter
import com.systematicshoppers.sysho.databinding.FragmentResultsBinding
import com.systematicshoppers.sysho.model.Datasource

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
            TODO("Currently the total price is based off of the product database." +
                    "Change to individual store database in the future.")

        }

        val mapView = view.findViewById<MapView>(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { googleMap ->
            val centerLatLng = LatLng(YOUR_LATITUDE, YOUR_LONGITUDE)
        }

        return view
    }

    override fun locationIntent() {
        TODO("Not yet implemented")
    }

}