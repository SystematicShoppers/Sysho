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
        holder.listNumber.text = "List ${position + 1}"

        holder.itemContainer.removeAllViews()
        userList.items.forEach { item ->
            val itemRow = LayoutInflater.from(holder.itemView.context).inflate(R.layout.item_row, holder.itemContainer, false)

            val itemName: TextView = itemRow.findViewById(R.id.item_name)
            val itemQuantity: TextView = itemRow.findViewById(R.id.item_quantity)

            itemName.text = item.entry
            itemQuantity.text = item.quantity.toString()

            holder.itemContainer.addView(itemRow)
        }
    }

    override fun getItemCount(): Int = items.size
}
