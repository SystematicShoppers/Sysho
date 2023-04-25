package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.TAG
import java.text.DecimalFormat

/**
 * Confirmation screen for a SaleDialog which is a dialog of ApiStoreSelectFragment.
 */
class SaleDialogConfirm: DialogFragment() {

    // Declare UI elements
    private lateinit var activateSaleWarningTextView: TextView
    private lateinit var activateSaleYesBtn: Button
    private lateinit var activateSaleNoBtn: Button
    // Get access to the shared ViewModel
    private val viewModel: SyshoViewModel by activityViewModels()

    /**
     * Inflate the dialog layout and handle user interactions
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.dialog_activate_sale_warning, container, false)

        // Initialize UI elements
        activateSaleWarningTextView = view.findViewById(R.id.activateSaleWarningTextView)
        activateSaleYesBtn = view.findViewById(R.id.activateSaleYesBtn)
        activateSaleNoBtn = view.findViewById(R.id.activateSaleNoBtn)

        // Set click listener for the "No" button
        activateSaleNoBtn.setOnClickListener{
            viewModel.setSalePercent(0.0)
            dismiss()
        }

        // Set click listener for the "Yes" button
        activateSaleYesBtn.setOnClickListener{
            // Get the sale percentage from the ViewModel
            val percent = viewModel.salePercent.value ?: 0.0

            // Update the sale prices in the database and notify the adapter
            setSale(percent) {
                viewModel.notifyApiStoreAdapter(true)
                dismiss()
            }
        }

        // Return the view
        return view
    }

    /**
     * Update the sale prices in the database and execute the callback
     */
    private fun setSale(percent: Double, callback: (Boolean) -> Unit) {
        val storeId = viewModel.store.value?.storeId
        if (storeId != null) {
            FirebaseUtils().fireStoreDatabase.collection("stores")
                .whereEqualTo("StoreID", storeId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0]
                        val stockArray = documentSnapshot.get("Stock")
                        if (stockArray is ArrayList<*>) {
                            if (stockArray.all { it is HashMap<*, *> }) {
                                @Suppress("UNCHECKED_CAST")
                                val updatedStockArray = updateStock(stockArray as ArrayList<HashMap<String, String>>, percent)
                                // Update "Stock" field in Firestore with the updated stockArray
                                documentSnapshot.reference.update("Stock", updatedStockArray)
                                    .addOnSuccessListener {
                                        callback.invoke(true)
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e(TAG, "Failed to update stock in Firestore: ${exception.message}")
                                        callback.invoke(false)
                                    }
                            } else {
                                Toast.makeText(context, "Unexpected HashMap for Product in Stock list.", Toast.LENGTH_SHORT).show()
                                callback.invoke(false)
                            }
                        } else {
                            Toast.makeText(context, "Stock list was not an array as expected.", Toast.LENGTH_SHORT).show()
                            callback.invoke(false)
                        }
                    } else {
                        callback.invoke(false)
                    }
                }
        } else {
            callback.invoke(false)
        }
    }

    /**
     * Apply the sale percentage to each item's price and return the updated stock array
     */
    private fun updateStock(stockArray: ArrayList<HashMap<String, String>>, percent: Double): ArrayList<HashMap<String, String>> {
        for (item in stockArray) {
            val price = item["Price"]
            try {
                var priceAsDouble = price?.toDouble()
                priceAsDouble = priceAsDouble?.minus((priceAsDouble * (percent / 100)))
                val df = DecimalFormat("#.00")
                item["Price"] = df.format(priceAsDouble)
            } catch (e: Exception) {
                Log.e(TAG, "Could not parse Firestore price (String) as Double.")
            }
        }
        return stockArray
    }
}