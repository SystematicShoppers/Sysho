package com.systematicshoppers.sysho.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        fun onMinusClick(position: Int, product: String): String
        fun onPlusClick(position: Int, product: String): String
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = queryList[position]
        holder.quantityTextView.text = 1.toString()
        holder.nameTextView.text = item.name
        holder.checkBox.isChecked = item.isChecked
        holder.checkBox.setOnClickListener {
            item.isChecked = !item.isChecked
            holder.checkBox.isChecked = item.isChecked
            onCLick.onCheckBoxClick()
        }
        holder.addBtn.setOnClickListener {
            holder.quantityTextView.text = onCLick.onPlusClick(holder.quantityTextView.text.toString().toInt(), holder.nameTextView.text.toString())
        }
        holder.minusBtn.setOnClickListener{
            holder.quantityTextView.text = onCLick.onMinusClick(holder.quantityTextView.text.toString().toInt(), holder.nameTextView.text.toString())
        }
    }


    override fun getItemCount(): Int {
        return queryList.size
    }
    fun removeItems(deleteList: MutableList<QueryItem>) {
        for (deleteItem in deleteList) {
            for (i in queryList.indices.reversed()) {
                if (queryList[i].name == deleteItem.name) {
                    queryList.removeAt(i)
                    notifyItemRemoved(i)
                }
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.queryItemTextView)
        val quantityTextView: TextView = view.findViewById(R.id.queryQuantityTextView)
        val minusBtn: Button = view.findViewById(R.id.minusBtn)
        val addBtn: Button = view.findViewById(R.id.addBtn)
        val checkBox: CheckBox = view.findViewById(R.id.queryItemCheckBox)
    }
}