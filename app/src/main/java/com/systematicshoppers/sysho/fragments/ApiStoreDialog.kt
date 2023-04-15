package com.systematicshoppers.sysho.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.systematicshoppers.sysho.R


class ApiStoreDialog : DialogFragment() {

    private lateinit var updatePriceBtn : Button
    private lateinit var saleBtn : Button
    private lateinit var priceSweepBtn : Button
    private lateinit var randomizeBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_api_store_menu, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updatePriceBtn = view.findViewById(R.id.updatePriceBtn)
        saleBtn = view.findViewById(R.id.saleBtn)
        priceSweepBtn = view.findViewById(R.id.offSaleBtn)
        randomizeBtn = view.findViewById(R.id.randFifteenBtn)

        updatePriceBtn.setOnClickListener {
            val updatePriceDialog = UpdatePriceDialog()
            updatePriceDialog.show(childFragmentManager, "update_price_dialog_tag")

        }

        saleBtn.setOnClickListener {
            val saleDialog = SaleDialog()
            saleDialog.show(childFragmentManager,"sale_dialog_tag")
        }

        priceSweepBtn.setOnClickListener {
            val sweepDialog = SweepDialog()
            sweepDialog.show(childFragmentManager, "sweep_dialog_tag")
        }

        randomizeBtn.setOnClickListener {

            dismiss()
        }

    }

}
