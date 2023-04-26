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
 * SweepDialogConfirm is a DialogFragment that acts as a confirmation screen
 * for SweepDialog which is a Dialog of ApiStoreSelectFragment.
 * It shows a warning message and two buttons: Yes and No.
 */
class SweepDialogConfirm: DialogFragment() {

    // UI elements declarations
    private lateinit var activateSweepWarningTextView: TextView
    private lateinit var activateSweepYesBtn: Button
    private lateinit var activateSweepNoBtn: Button

    // Access the shared ViewModel
    private val viewModel: SyshoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.dialog_activate_sweep_warning, container, false)

        // Initialize UI elements
        activateSweepWarningTextView = view.findViewById(R.id.activateSweepWarningTextView)
        activateSweepYesBtn = view.findViewById(R.id.activateSweepYesBtn)
        activateSweepNoBtn = view.findViewById(R.id.activateSweepNoBtn)

        // Set a click listener for the No button
        activateSweepNoBtn.setOnClickListener{
            // Reset salePercent to 0.0 and dismiss the dialog
            viewModel.setSalePercent(0.0)
            dismiss()
        }

        // Set a click listener for the Yes button
        activateSweepYesBtn.setOnClickListener{
            // Get the sale percentage from the ViewModel
            val percent = viewModel.salePercent.value ?: 0.0

            // Call setSweep function with the percentage and a callback function
            setSweep(percent) {
                // Notify the ApiStoreAdapter and dismiss the dialog
                viewModel.notifyApiStoreAdapter(true)
                dismiss()
            }
        }

        return view
    }

    /**
     * setSweep function updates the prices in Firestore based on the given percentage.
     */
    private fun setSweep(percent: Double, callback: (Boolean) -> Unit) {
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
     * updateStock function updates the prices in the stockArray based on the given percentage.
     * @param stockArray The list of products with their details as HashMaps.
     * @param percent The percentage to update the prices.
     * @return The updated stockArray with new prices.
     */
    private fun updateStock(stockArray: ArrayList<HashMap<String, String>>, percent: Double): ArrayList<HashMap<String, String>> {
        for (item in stockArray) {
            val price = item["Price"]
            try {
                var priceAsDouble = price?.toDouble()
                priceAsDouble = priceAsDouble?.plus((priceAsDouble * (percent / 100)))
                val df = DecimalFormat("#.00")
                item["Price"] = df.format(priceAsDouble)
            } catch (e: Exception) {
                Log.e(TAG, "Could not parse Firestore price (String) as Double.")
            }
        }
        return stockArray
    }
}