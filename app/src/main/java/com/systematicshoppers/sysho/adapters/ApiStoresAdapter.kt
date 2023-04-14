package com.systematicshoppers.sysho.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.database.Store

class ApiStoresAdapter(private val context: Context, private val data: MutableList<Store>, private val onCLick: ClickListener): RecyclerView.Adapter<ApiStoresAdapter.ApiStoresViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiStoresViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.api_store_item, parent,false)
        return ApiStoresViewHolder(view)
    }

    interface ClickListener {
        fun gotoStore(position: Int, storeData: Store)
    }

    override fun onBindViewHolder(holder: ApiStoresViewHolder, position: Int) {
        val data = data[position]
            holder.storeIDTextView.text = data.storeId
            holder.storeNameTextView.text = data.store
            holder.locationTextView.text = data.address
            holder.itemView.setOnClickListener {
                onCLick.gotoStore(position, data)
            }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ApiStoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeIDTextView: TextView = itemView.findViewById(R.id.storeIDTextView)
        val storeNameTextView: TextView = itemView.findViewById(R.id.storeNameTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)

    }

}