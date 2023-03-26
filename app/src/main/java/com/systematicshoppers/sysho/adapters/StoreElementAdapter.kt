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

class StoreElementAdapter(
    private val context: ResultsFragment,
    private val dataset: List<Store>
    ) :  RecyclerView.Adapter<StoreElementAdapter.StoreElementViewHolder>() {

    class StoreElementViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.store_element_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreElementViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.store_element, parent, false)

        return StoreElementViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: StoreElementViewHolder, position: Int) {
        val item = dataset[position]
        holder.textView.text = item.stringResource

    }

    override fun getItemCount() = dataset.size
}