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
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.SetOptions
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.TAG
import java.text.DecimalFormat


class SaleDialogConfirm: DialogFragment() {

    private lateinit var activateSaleWarningTextView: TextView
    private lateinit var activateSaleYesBtn: Button
    private lateinit var activateSaleNoBtn: Button
    private val viewModel: SyshoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_activate_sale_warning, container, false)
        activateSaleWarningTextView = view.findViewById(R.id.activateSaleWarningTextView)
        activateSaleYesBtn = view.findViewById(R.id.activateSaleYesBtn)
        activateSaleNoBtn = view.findViewById(R.id.activateSaleNoBtn)
        activateSaleNoBtn.setOnClickListener{
            viewModel.setSalePercent(0.0)
            dismiss()
        }
        activateSaleYesBtn.setOnClickListener{
            val percent = viewModel.salePercent.value ?: 0.0
            setSale(percent) {
                dismiss()
            }
        }

        return view
    }

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