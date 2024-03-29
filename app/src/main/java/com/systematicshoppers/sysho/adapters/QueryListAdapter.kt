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

/**
 * RecyclerView adapter used by the SearchFragment to display the listed products.
 * Checkboxes are clickable to delete selected items.
 * The plus and minus button are clickable on each item to change their quantity.
 * There is an added function removeItems which notifies the adapter when items were removed.
 */
class QueryListAdapter(
    private val queryList: MutableList<QueryItem>,
    private val onCLick: ClickListener
    ) : RecyclerView.Adapter<QueryListAdapter.ViewHolder>() {

    /**
     * Creates a new ViewHolder with the query_item layout and returns it.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.query_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * ClickListener interface to handle the click events on the checkboxes, plus, and minus buttons in the RecyclerView.
     */
    interface ClickListener {
        fun onCheckBoxClick()
        fun onMinusClick(position: Int, product: String): String
        fun onPlusClick(position: Int, product: String): String
    }

    /**
     * Binds the data to the views in the ViewHolder and sets onClickListeners for checkboxes, plus, and minus buttons.
     */
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

    /**
     * Returns the total number of items in the RecyclerView.
     */
    override fun getItemCount(): Int {
        return queryList.size
    }

    /**
     * Remove items from the queryList and notify the adapter of the change.
     */
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

    /**
     * ViewHolder class for the adapter, which holds the references to the views in the query_item layout.
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.queryItemTextView)
        val quantityTextView: TextView = view.findViewById(R.id.queryQuantityTextView)
        val minusBtn: Button = view.findViewById(R.id.minusBtn)
        val addBtn: Button = view.findViewById(R.id.addBtn)
        val checkBox: CheckBox = view.findViewById(R.id.queryItemCheckBox)
    }
}