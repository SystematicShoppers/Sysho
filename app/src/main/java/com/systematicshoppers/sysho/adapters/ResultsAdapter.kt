package com.systematicshoppers.sysho.adapters

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.database.Coordinates
import com.systematicshoppers.sysho.database.FirebaseLocationUtils
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.Store
import java.util.*

class ResultsAdapter(private val context: Context,
                     private val data: MutableList<Store>,
                     private val onCLick: ClickListener): RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.results_layout, parent,false)
        return ResultsViewHolder(view)
    }

    interface ClickListener {
        fun gotoMap(address: String, coordinates: Coordinates)
    }


    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        holder.storeName.text = data[position].store
        holder.address.text = data[position].address
        holder.distance.text = String.format("%.2f", data[position].distance)
        holder.total.text = data[position].totalPrice.toString()
        holder.itemView.setOnClickListener{
            val coordinates = getCoordinates(data[position])
            onCLick.gotoMap(holder.address.text.toString(), coordinates)
        }
    }

    private fun getCoordinates(store: Store): Coordinates {
        val lat = store.latitude
        val long = store.longitude
        var coordinates = Coordinates(0.0, 0.0)
        if(lat != "null" && long != "null") {
            coordinates = Coordinates(lat?.toDouble(), long?.toDouble())
        }
        return coordinates
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeName: TextView = itemView.findViewById(R.id.storeName)
        val address: TextView = itemView.findViewById(R.id.address)
        val distance: TextView = itemView.findViewById(R.id.distance)
        val total: TextView = itemView.findViewById(R.id.total)
    }

}