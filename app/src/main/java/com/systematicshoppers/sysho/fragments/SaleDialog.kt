package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel

/**
 * A dialog for ApiStoreSelectFragment if the user chooses to use the on sale algorithm.
 * On sale reduces all prices in the store by a set percentage.
 */
class SaleDialog: DialogFragment() {

    // Declare UI elements
    private lateinit var activateSaleTextView: TextView
    private lateinit var activateSaleEditTextNumber: EditText
    private lateinit var activateSaleConfirmBtn: Button
    private lateinit var closeActivateSaleDialog: ImageView

    // Get access to the shared ViewModel
    private val viewModel: SyshoViewModel by activityViewModels()

    /**
     * Inflates the dialog layout and handles user interactions
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.dialog_activate_sale, container, false)

        // Initialize UI elements
        activateSaleTextView = view.findViewById(R.id.activateSaleTextView)
        activateSaleEditTextNumber = view.findViewById(R.id.activateSaleEditTextNumber)
        activateSaleConfirmBtn = view.findViewById(R.id.activateSaleConfirmBtn)
        closeActivateSaleDialog = view.findViewById(R.id.closeActivateSaleDialog)

        // Set the focus on the EditText
        activateSaleEditTextNumber.requestFocus()
        var inputText = ""
        var percent = 0.0

        // Set click listener for closing the dialog
        closeActivateSaleDialog.setOnClickListener{
            dismiss()
        }

        // Set click listener for the confirmation button
        activateSaleConfirmBtn.setOnClickListener {
            inputText = activateSaleEditTextNumber.text.toString()  // Get the user input

            // Check if input is not empty
            if (inputText.isNotEmpty()) {
                percent = inputText.toDoubleOrNull() ?: 0.0                              // Parse the input to a double or set it to 0.0 if not valid
                viewModel.setSalePercent(percent)                                        // Save the sale percentage in the ViewModel
                SaleDialogConfirm().show(parentFragmentManager, "confirm sale price")// Show the confirmation dialog
                activateSaleEditTextNumber.text.clear()                                  // Clear the input field and close the dialog
                dismiss()
            } else {
                // Clear the input field and show an error message
                activateSaleEditTextNumber.text.clear()
                Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Return the view
        return view
    }
}