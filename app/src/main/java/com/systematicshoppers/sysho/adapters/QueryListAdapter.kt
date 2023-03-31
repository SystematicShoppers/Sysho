package com.systematicshoppers.sysho.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.database.QueryItem


class QueryListAdapter(private val queryList: MutableList<QueryItem>, private val onCLick: ClickListener) :
    RecyclerView.Adapter<QueryListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.query_item, parent, false)
        return ViewHolder(view)
    }

    interface ClickListener {
        fun onCheckBoxClick()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = queryList[position]
        holder.nameTextView.text = item.name
        holder.checkBox.isChecked = item.isChecked
        holder.checkBox.setOnClickListener {
            item.isChecked = !item.isChecked
            holder.checkBox.isChecked = item.isChecked
            onCLick.onCheckBoxClick()
        }
    }


    override fun getItemCount(): Int {
        return queryList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.queryItemTextView)
        val checkBox: CheckBox = view.findViewById(R.id.queryItemCheckBox)
    }
}