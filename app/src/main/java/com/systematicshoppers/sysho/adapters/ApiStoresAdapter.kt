package com.systematicshoppers.sysho.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.database.Store

class ApiStoresAdapter(private val context: Context, private val data: MutableList<Store>): RecyclerView.Adapter<ApiStoresAdapter.ApiStoresViewHolder>() {
    inner class ApiStoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeIDTextView: TextView = itemView.findViewById(R.id.storeIDTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val storeNameTextView: TextView = itemView.findViewById(R.id.storeNameTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiStoresViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.store_item, parent,false)
        return ApiStoresViewHolder(view)
    }

    interface ClickListener {
        fun gotoStore(position: Int, storeData: Store)
    }

    override fun onBindViewHolder(holder: ApiStoresViewHolder, position: Int) {
        val data = data[position]
        holder.storeIDTextView.text = data.storeId
        holder.storeNameTextView.text = data.store
        holder.locationTextView.text = data.address?.getAddressLine(0)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}