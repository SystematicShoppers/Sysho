package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.TAG
import java.text.DecimalFormat

/**
 * Dialog for updating a price by a set amount entered by the user.
 * Helper function updatePrice() will check if the entered price is a proper format and
 * update Firebase accordingly.
 */
class UpdatePriceDialog: DialogFragment() {

    // Access the shared ViewModel
    val viewModel: SyshoViewModel by activityViewModels()

    /**
     * Inflates the layout for UpdatePriceDialog and sets the required click listeners.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_api_store_select_dialog, container)
        val editText = view.findViewById<EditText>(R.id.priceEditText)
        val currentPrice = view.findViewById<TextView>(R.id.dialogCurrentPriceTextView)
        val updateBtn = view.findViewById<Button>(R.id.dialogUpdateNewPriceBtn)
        val closeBtn = view.findViewById<ImageView>(R.id.closeUpdatePriceDialog)
        currentPrice.text = getString(R.string.current_price)
        dialog?.setTitle("Update Price")
        editText.requestFocus()

        // Sets click listener for the update button and triggers the updatePrice method.
        updateBtn.setOnClickListener{
            viewModel.setDialogEditText(editText.text.toString())
            if (viewModel.dialogEditText.value != null) {
                updatePrice() {}
                viewModel.notifyApiStoreAdapter(true)
                Toast.makeText(requireContext(), "Price has been updated!", Toast.LENGTH_SHORT).show()
            }
        }

        // Sets click listener for the close button, dismissing the dialog.
        closeBtn.setOnClickListener {
            dismiss()
        }

        viewModel.product.observe(viewLifecycleOwner) {
            currentPrice.text = (buildString {
                append(getString(R.string.current_price))
                append(" ")
                append(viewModel.product.value?.price)
            })
        }
        return view
    }

    /**
     * updatePrice function updates the price of a single product in the Firebase Firestore.
     * @param callback Callback function to be executed after the price update is complete.
     */
    private fun updatePrice(callback: (Boolean) -> Unit) {
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
                                val updatedStockArray = updateStockAt(
                                    stockArray as ArrayList<HashMap<String, String>>,
                                    viewModel.product.value?.id
                                )
                                // Update "Stock" field in Firestore with the updated stockArray
                                documentSnapshot.reference.update("Stock", updatedStockArray)
                                    .addOnSuccessListener {
                                        callback.invoke(true)
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e(
                                            TAG,
                                            "Failed to update stock in Firestore: ${exception.message}"
                                        )
                                        callback.invoke(false)
                                    }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Unexpected HashMap for Product in Stock list.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                callback.invoke(false)
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Stock list was not an array as expected.",
                                Toast.LENGTH_SHORT
                            ).show()
                            callback.invoke(false)
                        }
                    }
                }
        }
    }

    /**
     * updateStockAt function updates the price of a single product in the stock array.
     * @param stockArray The stock array containing products with their details.
     * @param id The ID of the product whose price needs to be updated.
     * @return Returns the updated stock array with the new price for the product.
     */
    private fun updateStockAt(stockArray: ArrayList<HashMap<String, String>>, id: String?): ArrayList<HashMap<String, String>> {
        for (item in stockArray) {
            if( item["ID"] == id) {
                try {
                    val newPrice = viewModel.dialogEditText.value.toString()
                    val priceAsDouble: Double = newPrice.toDouble()
                    val df = DecimalFormat("#.00")
                    item["Price"] = df.format(priceAsDouble)
                    return stockArray
                } catch (e: Exception) {
                    Log.e(TAG, "Could not parse Firestore price (String) as Double.")
                }
            }
        }
        return stockArray
    }
}