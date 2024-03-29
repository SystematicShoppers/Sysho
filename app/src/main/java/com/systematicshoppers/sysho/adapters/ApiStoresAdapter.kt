package com.systematicshoppers.sysho.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.database.Store
/**
 * Standard RecyclerView adapter for the Store database of the API.
 * Also has an added function for displaying logos. Items are clickable.
 */
class ApiStoresAdapter(private val context: Context, private val data: MutableList<Store>, private val onCLick: ClickListener): RecyclerView.Adapter<ApiStoresAdapter.ApiStoresViewHolder>() {

    /**
     * This method creates a new ViewHolder with the api_store_item layout and returns it.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiStoresViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.api_store_item, parent,false)
        return ApiStoresViewHolder(view)
    }

    /**
     * ClickListener interface to handle the click events on the items in the RecyclerView.
     */
    interface ClickListener {
        fun gotoStore(position: Int, storeData: Store)

    }

    /**
     * This method binds the data to the views in the ViewHolder and sets an onClickListener for each item.
     */
    override fun onBindViewHolder(holder: ApiStoresViewHolder, position: Int) {
            val data = data[position]
            holder.storeIDTextView.text = data.storeId
            holder.storeNameTextView.text = data.store
            holder.locationTextView.text = data.address
            holder.logoImageView.setImageResource(setLogo(data.store))
            holder.itemView.setOnClickListener {
                onCLick.gotoStore(position, data)
            }
    }

    /**
     * This method returns the total number of items in the RecyclerView.
     */
    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * ViewHolder class for the adapter, which holds the references to the views in the api_store_item layout.
     */
    inner class ApiStoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeIDTextView: TextView = itemView.findViewById(R.id.storeIDTextView)
        val storeNameTextView: TextView = itemView.findViewById(R.id.storeNameTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val logoImageView: ImageView = itemView.findViewById(R.id.logo)

    }

    /**
     * This private method sets the store logo based on the store name and returns the corresponding drawable resource.
     */
    private fun setLogo(store: String?): Int {
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
}