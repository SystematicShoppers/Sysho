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
 * SweepDialog is a DialogFragment for the ApiStoreSelectFragment to increase all prices in a store by a certain percentage.
 * The dialog consists of an EditText for entering the percentage value and buttons for confirming and closing the dialog.
 */
class SweepDialog: DialogFragment() {

    // UI elements declarations
    private lateinit var activateSweepTextView: TextView
    private lateinit var activateSweepEditTextNumber: EditText
    private lateinit var activateSweepConfirmBtn: Button
    private lateinit var closeActivateSweepDialog: ImageView

    // Access the shared ViewModel
    private val viewModel: SyshoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.dialog_activate_sweep, container, false)

        // Initialize UI elements
        activateSweepTextView = view.findViewById(R.id.activateSweepTextView)
        activateSweepEditTextNumber = view.findViewById(R.id.activateSweepEditTextNumber)
        activateSweepConfirmBtn = view.findViewById(R.id.activateSweepConfirmBtn)
        closeActivateSweepDialog = view.findViewById(R.id.closeActivateSweepDialog)

        // Request focus on the EditText
        activateSweepEditTextNumber.requestFocus()

        // Variables to store user input
        var inputText: String
        var percent: Double

        // Set a click listener for the close button
        closeActivateSweepDialog.setOnClickListener{
            dismiss()   // Dismiss the dialog
        }

        // Set a click listener for the confirm button
        activateSweepConfirmBtn.setOnClickListener {
            // Get the input text
            inputText = activateSweepEditTextNumber.text.toString()

            // Check if the input is not empty
            if (inputText.isNotEmpty()) {
                // Try to convert the input to a double value, or use 0.0 if it's not a valid number
                percent = inputText.toDoubleOrNull() ?: 0.0

                // Set the sale percentage in the shared ViewModel
                viewModel.setSalePercent(percent)

                // Show a confirmation dialog
                SweepDialogConfirm().show(parentFragmentManager, "confirm sale price")

                // Clear the input field
                activateSweepEditTextNumber.text.clear()

                // Dismiss the dialog
                dismiss()
            } else {
                // Clear the input field and show a message if the input is empty
                activateSweepEditTextNumber.text.clear()
                Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return view
    }
}