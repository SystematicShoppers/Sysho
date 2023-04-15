package com.systematicshoppers.sysho.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.database.UserList

class ListItemAdapter(private val items: List<UserList>) :
    RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listNumber: TextView = view.findViewById(R.id.list_number)
        val itemContainer: LinearLayout = view.findViewById(R.id.item_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userList = items[position]

        holder.listNumber.text = "List #${position + 1}"

        holder.itemContainer.removeAllViews()

        for (item in userList.items) {
            val itemNameView = TextView(holder.itemContainer.context).apply {
                text = item.entry
                textSize = 16f
            }
            val itemQuantityView = TextView(holder.itemContainer.context).apply {
                text = item.quantity.toString()
                textSize = 16f
            }

            val itemRow = LinearLayout(holder.itemContainer.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                weightSum = 2f
                addView(itemNameView, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                addView(itemQuantityView, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            }

            holder.itemContainer.addView(itemRow)
        }
    }

    override fun getItemCount(): Int = items.size
}
