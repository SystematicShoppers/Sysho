package com.systematicshoppers.sysho.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.systematicshoppers.sysho.R

/**
 * A dialog pop up for the ApiStoreFragment.
 * Displays a selection of price manipulation algorithms as buttons in the pop-up display.
 */
class ApiStoreDialog : DialogFragment() {

    // Declare buttons for the different price manipulation options.
    private lateinit var updatePriceBtn : Button
    private lateinit var saleBtn : Button
    private lateinit var priceSweepBtn : Button

    /**
     * Inflate the layout for the dialog
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_api_store_menu, container, false)
    }

    /**
     * Set the dialog's background to transparent and set its layout parameters
     */
    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * Initialize the buttons and set their click listeners to show the respective dialog fragments
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updatePriceBtn = view.findViewById(R.id.updatePriceBtn)
        saleBtn = view.findViewById(R.id.saleBtn)
        priceSweepBtn = view.findViewById(R.id.offSaleBtn)

        // Show the UpdatePriceDialog when the updatePriceBtn is clicked.
        updatePriceBtn.setOnClickListener {
            val updatePriceDialog = UpdatePriceDialog()
            updatePriceDialog.show(childFragmentManager, "update_price_dialog_tag")

        }

        // Show the SaleDialog when the saleBtn is clicked.
        saleBtn.setOnClickListener {
            val saleDialog = SaleDialog()
            saleDialog.show(childFragmentManager,"sale_dialog_tag")
        }

        // Show the SweepDialog when the priceSweepBtn is clicked.
        priceSweepBtn.setOnClickListener {
            val sweepDialog = SweepDialog()
            sweepDialog.show(childFragmentManager, "sweep_dialog_tag")
        }
    }
}