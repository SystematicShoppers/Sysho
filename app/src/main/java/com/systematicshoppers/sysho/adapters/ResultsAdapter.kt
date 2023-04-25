package com.systematicshoppers.sysho.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.database.Coordinates
import com.systematicshoppers.sysho.database.Store
import java.text.NumberFormat
import java.util.*

/**
 * RecyclerView adapter that lists the results of a search.
 * Items are clickable and the click opens an intent to Google Maps for navigation.
 */
class ResultsAdapter(
    private val context: Context,
    private val data: MutableList<Store>,
    private val onCLick: ClickListener
    ) : RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>() {

    /**
     * Creates a new ResultsViewHolder with the results_layout and returns it.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.results_layout, parent,false)
        return ResultsViewHolder(view)
    }

    /**
     * ClickListener interface to handle the click events on the results items in the RecyclerView.
     */
    interface ClickListener {
        fun gotoMap(address: String, coordinates: Coordinates)
    }

    /**
     * Set the logo in a results item if it matches a popular store.
     */
    private fun setLogo(store: String?, view: View): Int {
        when (store) {
            "WalMart", "Wal Mart", "Wal-Mart", "Wal - Mart", "Walmart" ->
                return R.drawable.walmart_logo
            "Aldi", "aldi" ->
                return R.drawable.aldi_logo
            "Target", "target" ->
                return R.drawable.target_logo
            "Winn Dixie", "winn dixie", "Winn-Dixie", "winn-dixie" ->
                return R.drawable.winn_dixie_logo
            "Publix", "publix" ->
                return R.drawable.publix_logo
            "Trader Joe's", "trader joe's", "trader joes", "Trader Joes" ->
                return R.drawable.trader_joes_logo
            else ->
                return android.R.color.transparent
        }
    }

    /**
     * Binds the data to the views in the ResultsViewHolder and sets an onClickListener for each item.
     */
    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        holder.storeName.text = data[position].store
        holder.address.text = data[position].address
        holder.logo.setImageResource(setLogo(holder.storeName.text.toString(), holder.itemView))
        holder.distance.text = String.format("%.2f", data[position].distance) + " miles"
        val locale = Locale.US // Specify the desired locale for dollar format
        val currencyFormatter = NumberFormat.getCurrencyInstance(locale)
        currencyFormatter.minimumFractionDigits = 2
        currencyFormatter.maximumFractionDigits = 2
        val formattedPrice = currencyFormatter.format(data[position].totalPrice)
        holder.total.text = formattedPrice
        holder.itemView.setOnClickListener{
            val coordinates = getCoordinates(data[position])
            onCLick.gotoMap(holder.address.text.toString(), coordinates)
        }
    }

    /**
     * Converts the lat and long variables of a Store object to a Coordinate object that can be used by Google Maps navigation.
     */
    private fun getCoordinates(store: Store): Coordinates {
        val lat = store.latitude
        val long = store.longitude
        var coordinates = Coordinates(0.0, 0.0)
        if(lat != "null" && long != "null") {
            coordinates = Coordinates(lat?.toDouble(), long?.toDouble())
        }
        return coordinates
    }

    /**
     * Returns the total number of items in the RecyclerView.
     */
    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * ViewHolder class for the adapter, which holds the references to the views in the results_layout.
     */
    inner class ResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeName: TextView = itemView.findViewById(R.id.storeName)
        val address: TextView = itemView.findViewById(R.id.address)
        val distance: TextView = itemView.findViewById(R.id.distance)
        val total: TextView = itemView.findViewById(R.id.total)
        val logo: ImageView = itemView.findViewById(R.id.logo)
    }
}