package com.systematicshoppers.sysho.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.database.Product

class ApiProductsAdapter(private val context: Context, private val data: MutableList<Product>): RecyclerView.Adapter<ApiProductsAdapter.ApiProductsViewHolder>() {
    class ApiProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productIDTextView: TextView = itemView.findViewById(R.id.productIDTextView)
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)

    }

    interface ClickListener {
        fun gotoProduct(position: Int, productData: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiProductsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_item, parent,false)
        return ApiProductsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApiProductsViewHolder, position: Int) {
        val data = data[position]
        holder.productIDTextView.text = "Product ID: ${data.id.toString()}"
        holder.productNameTextView.text = "${data.productName}"
    }

    override fun getItemCount(): Int {
        return data.size
    }

}

