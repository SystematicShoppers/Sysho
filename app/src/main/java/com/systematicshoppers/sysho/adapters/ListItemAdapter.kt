package com.systematicshoppers.sysho.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.database.UserList

/**
 * ListItemAdapter.kt is a custom RecyclerView adapter that is used to display a list of UserList objects.
 * Each UserList object is displayed as a list with a number, items, and a delete button.
 **/
class ListItemAdapter(private val items: List<UserList>, private val onDelete: (Int) -> Unit) :
    RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {

    /**
     * The ViewHolder inner class is used to hold references to the views that will be used to display the data.
     * It contains three views: a TextView to display the list number, a LinearLayout to hold the items in the list,
     * and an ImageButton for the delete button.
     **/
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listNumber: TextView = view.findViewById(R.id.list_number)
        val itemContainer: LinearLayout = view.findViewById(R.id.item_container)
        val deleteListButton: ImageButton = view.findViewById(R.id.delete_list_button)
    }

    /**
     * The onCreateViewHolder function inflates the list_item_layout layout,
     * which defines the structure of a single list item in the RecyclerView,
     * and returns a ViewHolder containing references to the views in the layout.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_layout, parent, false)
        return ViewHolder(view)
    }

    /**
     * The onBindViewHolder function is called to populate the views in the ViewHolder with the data from the UserList object
     * at the given position in the items list. It sets the list number, iterates over the items in the UserList,
     * inflating the item_row layout for each item, and populates the item name and quantity. Then, it adds each item row to the
     * itemContainer LinearLayout. The onDelete lambda function is set to be called when the delete button is clicked, passing
     * the position of the list item in the RecyclerView.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the UserList object at the current position
        val userList = items[position]
        // Set the list number text view to display the list number
        holder.listNumber.text = "List ${position + 1}"

        // Remove all views from the item container
        holder.itemContainer.removeAllViews()
        // Iterate through each item in the UserList object
        userList.items.forEach { item ->
            // Inflate the item_row layout for each item in the UserList
            val itemRow = LayoutInflater.from(holder.itemView.context).inflate(R.layout.item_row, holder.itemContainer, false)

            // Get references to the item_name and item_quantity text views in the item_row layout
            val itemName: TextView = itemRow.findViewById(R.id.item_name)
            val itemQuantity: TextView = itemRow.findViewById(R.id.item_quantity)

            // Set the text of the item_name and item_quantity text views
            itemName.text = item.entry
            itemQuantity.text = item.quantity.toString()

            // Add the item_row layout to the item container
            holder.itemContainer.addView(itemRow)
        }

        // Set an onClickListener for the deleteListButton to call the onDelete function with the current position
        holder.deleteListButton.setOnClickListener {
            onDelete(position)
        }
    }


    /**
     * The getItemCount function returns the number of items in the items list,
     * which determines the number of items displayed in the RecyclerView.
     */
    override fun getItemCount(): Int = items.size
}
