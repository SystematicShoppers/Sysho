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


class SweepDialog: DialogFragment() {

    private lateinit var activateSweepTextView: TextView
    private lateinit var activateSweepEditTextNumber: EditText
    private lateinit var activateSweepConfirmBtn: Button
    private lateinit var closeActivateSweepDialog: ImageView

    private val viewModel: SyshoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_activate_sweep, container, false)
        activateSweepTextView = view.findViewById(R.id.activateSweepTextView)
        activateSweepEditTextNumber = view.findViewById(R.id.activateSweepEditTextNumber)
        activateSweepConfirmBtn = view.findViewById(R.id.activateSweepConfirmBtn)
        closeActivateSweepDialog = view.findViewById(
            R.id.closeActivateSweepDialog
        )
        activateSweepEditTextNumber.requestFocus()
        var inputText: String
        var percent: Double

        closeActivateSweepDialog.setOnClickListener{
            dismiss()
        }
        activateSweepConfirmBtn.setOnClickListener {
            inputText = activateSweepEditTextNumber.text.toString()
            if (inputText.isNotEmpty()) {
                percent = inputText.toDoubleOrNull() ?: 0.0
                viewModel.setSalePercent(percent)
                SweepDialogConfirm().show(parentFragmentManager, "confirm sale price")
            } else {
                activateSweepEditTextNumber.text.clear()
                Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }
}