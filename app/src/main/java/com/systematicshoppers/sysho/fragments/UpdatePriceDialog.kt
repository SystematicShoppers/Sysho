package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel


class UpdatePriceDialog: DialogFragment() {

    val viewModel: SyshoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_api_store_select_dialog, container)
        val editText = view.findViewById<EditText>(R.id.priceEditText)
        val currentPrice = view.findViewById<TextView>(R.id.dialogCurrentPriceTextView)
        val salePrice = view.findViewById<TextView>(R.id.dialogPriceUpdateTextView)
        val updateBtn = view.findViewById<Button>(R.id.dialogUpdateNewPriceBtn)
        currentPrice.text = getString(R.string.current_price)
        salePrice.text = getString(R.string.sale_price)
        dialog?.setTitle("Update Price")
        editText.requestFocus()


        updateBtn.setOnClickListener{
            salePrice.text = "" //resets string value to prevent duplicate entries displaying
            viewModel.setDialogEditText(editText.text.toString())
        }
        viewModel.product.observe(viewLifecycleOwner) {
            currentPrice.text = (buildString {
                append(getString(R.string.current_price))
                append(" ")
                append(viewModel.product.value?.price)
            })
        }
        viewModel.dialogEditText.observe(viewLifecycleOwner) {
            salePrice.text = (buildString {
                append(getString(R.string.sale_price))
                append(" ")
                append(viewModel.dialogEditText.value)
            })
        }
        return view
    }
}