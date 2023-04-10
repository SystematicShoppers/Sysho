package com.systematicshoppers.sysho.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.fragments.ApiStoreSelectDialogFragment

class ApiStoresSelectAdapter(private val context: Context,
                             private val data: List<MutableMap<String, Any>>?,
                             private val onCLick: ClickListener) :  RecyclerView.Adapter<ApiStoresSelectAdapter.ApiStoresSelectViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiStoresSelectViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.api_product_item, parent,false)
        return ApiStoresSelectViewHolder(view)
    }

    interface ClickListener {
        fun modifyProduct(position: Int, productData: MutableMap<String, Any>?)
        fun updatePrice(position: Int, productData: MutableMap<String, Any>?)
    }

    override fun onBindViewHolder(holder: ApiStoresSelectViewHolder, position: Int) {
        val data = data?.get(position)
        holder.productIDTextView.text = "Product ID: ${data?.get("ID").toString()}"
        holder.productNameTextView.text = "${data?.get("ProductName").toString()}"
        holder.priceTextView.text = "$${data?.get("Price").toString()}"
        holder.itemView.setOnClickListener{
            onCLick.modifyProduct(position, data)
        }
        holder.updateBtn.setOnClickListener {
            onCLick.updatePrice(position, data)
        }
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    inner class ApiStoresSelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val updateBtn: ImageView = itemView.findViewById(R.id.changePriceBtn)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val productIDTextView: TextView = itemView.findViewById(R.id.productIDTextView)
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
    }

}