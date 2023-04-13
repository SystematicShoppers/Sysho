package com.systematicshoppers.sysho.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.databinding.DialogActivateSaleBinding

class SaleDialog: DialogFragment() {

    private lateinit var activateSaleTextView: TextView
    private lateinit var activateSaleEditTextNumber: EditText
    private lateinit var activateSaleConfirmBtn: Button
    private lateinit var closeActivateSaleDialog: ImageView

    private val viewModel: SyshoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_activate_sale, container, false)
        activateSaleTextView = view.findViewById(R.id.activateSaleTextView)
        activateSaleEditTextNumber = view.findViewById(R.id.activateSaleEditTextNumber)
        activateSaleConfirmBtn = view.findViewById(R.id.activateSaleConfirmBtn)
        closeActivateSaleDialog = view.findViewById(
            R.id.closeActivateSaleDialog
        )
        activateSaleEditTextNumber.requestFocus()
        var inputText = ""
        var percent = 0.0

        closeActivateSaleDialog.setOnClickListener{
            dismiss()
        }
        activateSaleConfirmBtn.setOnClickListener {
            inputText = activateSaleEditTextNumber.text.toString()
            if (inputText.isNotEmpty()) {
                percent = inputText.toDoubleOrNull() ?: 0.0
                viewModel.setSalePercent(percent)
                SaleDialogConfirm().show(parentFragmentManager, "confirm sale price")
            } else {
                activateSaleEditTextNumber.text.clear()
                Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }
}


