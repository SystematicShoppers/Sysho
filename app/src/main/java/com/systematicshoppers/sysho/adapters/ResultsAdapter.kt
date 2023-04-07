package com.systematicshoppers.sysho.adapters

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.database.Coordinates
import com.systematicshoppers.sysho.database.FirebaseLocationUtils
import com.systematicshoppers.sysho.database.FirebaseUtils
import java.util.*

class ResultsAdapter(private val context: Context,
                     private val activity: FragmentActivity,
                     private val coordinates: MutableList<Coordinates>,
                     //private val totalPrice: List<Double>,
                     private val onCLick: ClickListener): RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.results_layout, parent,false)
        return ResultsViewHolder(view)
    }

    interface ClickListener {
        fun gotoMap(address: String, coordinates: Coordinates)
    }


    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        val coordinates = coordinates[position]
        //val totalPrice = totalPrice[position]
       FirebaseUtils().getStoreName(coordinates) { name ->
           holder.storeName.text = name
           println(name)
        }
        holder.address.text = "Loading..."
        FirebaseUtils().getAddress(coordinates, geocoder = Geocoder(context, Locale.getDefault())) { address ->
            holder.address.text = address
            println(address)
        }
        val distance = FirebaseLocationUtils(activity).getDistance(coordinates, geocoder = Geocoder(context, Locale.getDefault()))
        holder.distance.text = "${String.format("%.1f", distance)} miles"
        //holder.total.text = totalPrice.toString() //TODO: create a FirebaseUtil() function for totalPrice by store
        holder.itemView.setOnClickListener{
            onCLick.gotoMap(holder.address.text.toString(), coordinates)
        }
    }

    override fun getItemCount(): Int {
        return coordinates.size
    }

    inner class ResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeName: TextView = itemView.findViewById(R.id.storeName)
        val address: TextView = itemView.findViewById(R.id.address)
        val distance: TextView = itemView.findViewById(R.id.distance)
        //val total: TextView = itemView.findViewById(R.id.total)
    }

}