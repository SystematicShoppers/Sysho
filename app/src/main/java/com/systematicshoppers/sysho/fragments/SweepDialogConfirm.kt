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


class SweepDialogConfirm: DialogFragment() {

    private lateinit var activateSweepWarningTextView: TextView
    private lateinit var activateSweepYesBtn: Button
    private lateinit var activateSweepNoBtn: Button
    private val viewModel: SyshoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_activate_sweep_warning, container, false)
        activateSweepWarningTextView = view.findViewById(R.id.activateSweepWarningTextView)
        activateSweepYesBtn = view.findViewById(R.id.activateSweepYesBtn)
        activateSweepNoBtn = view.findViewById(R.id.activateSweepNoBtn)
        activateSweepNoBtn.setOnClickListener{
            viewModel.setSalePercent(0.0)
            dismiss()
        }
        activateSweepYesBtn.setOnClickListener{
            val percent = viewModel.salePercent.value ?: 0.0
            setSweep(percent) {
                viewModel.notifyApiStoreAdapter(true)
                dismiss()
            }
        }

        return view
    }

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