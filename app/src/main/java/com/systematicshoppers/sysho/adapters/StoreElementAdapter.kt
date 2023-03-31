//source for help in setting up the RecyclerView: https://developer.android.com/codelabs/basic-android-kotlin-training-affirmations-app#3
package com.systematicshoppers.sysho.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.fragments.ResultsFragment
import com.systematicshoppers.sysho.model.Store
//FIXME should I import the database store and use that instead of the above? Can I just use it like this:
//import com.systematicshoppers.sysho.database.Store

class StoreElementAdapter(private val context: Context,
                             private val data: List<MutableMap<String, Any>>?,
                             private val onCLick: ApiStoresSelectAdapter.ClickListener
) :  RecyclerView.Adapter<StoreElementAdapter.StoreElementViewHolder>() {

    interface ClickListener {

        fun locationIntent()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreElementViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.result_item, parent,false)

        return StoreElementViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreElementViewHolder, position: Int) {
        val data = data?.get(position)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    inner class StoreElementViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        // val textView: TextView = view.findViewById(R.id.result_item)
    }
}