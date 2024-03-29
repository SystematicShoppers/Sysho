package com.systematicshoppers.sysho.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.TAG

/**A fragment of ApiActivity that displays specific product data. Loads after an onclick event from any ApiProductsFragment recyclerView item.
 * Displays Product info and a graph for that products price points in all stores. In the future, as more stores are added, this data would be better
 * represents as a histograph. Currently a line graph. **/
class ApiProductSelectFragment: Fragment() {

    private val viewModel: SyshoViewModel by activityViewModels()
    private var priceData: MutableList<String> = mutableListOf()
    private lateinit var productName: String
    private lateinit var productId: String
    private lateinit var nameTextView: TextView
    private lateinit var idTextView: TextView
    private lateinit var chart: LineChart
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_api_product_select, container, false)
        nameTextView = view.findViewById(R.id.apiProductSelectName)
        idTextView = view.findViewById(R.id.apiProductSelectId)
        chart = view.findViewById(R.id.chart)
        productName = viewModel.product.value?.productName.toString()
        productId = viewModel.product.value?.id.toString()
        nameTextView.text = productName
        idTextView.text = productId
        /**
         *
         * priceDataCallback retrieves the store data... gets stock array from store data... gets maps from stock array
         * then iterates through the maps looking for keys that match the product name that was clicked on
         * if it matches then the price is added to a mutableList.
         * **/
        /** Side note: This may be confusing, but the data from the callback is not the global variable 'priceData'
         * It is a version of it converted to Doubles (from Strings). Just in case anyone is wondering... for debugging purposes.
         * **/
        priceDataCallback { data ->
            // Create an ArrayList to hold Entry objects for plotting
            val entries = ArrayList<Entry>()
            for (i in 0 until data.size) {
                entries.add(Entry(i.toFloat(), data[i].toFloat()))
            }

            val dataSet = LineDataSet(entries, "Price Point for all Stores")
            dataSet.color = Color.BLUE
            dataSet.setCircleColor(Color.TRANSPARENT)
            dataSet.lineWidth = 1.5f
            dataSet.circleRadius = 0f
            dataSet.setDrawCircleHole(false)
            dataSet.valueTextSize = 9f
            dataSet.setDrawValues(false)

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(dataSet)

            val lineData = LineData(dataSets)
            chart.data = lineData
            chart.description.isEnabled = false
            chart.setTouchEnabled(true)
            chart.setPinchZoom(true)
            chart.isDoubleTapToZoomEnabled = true
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            chart.xAxis.valueFormatter = IndexAxisValueFormatter()
            chart.axisRight.isEnabled = false
            chart.axisLeft.spaceTop = 10f
            chart.animateX(1500)
            chart.invalidate()
        }
        return view
    }

    private fun priceDataCallback(callback: (MutableList<Double>) -> Unit) {
        val priceDataToDouble: MutableList<Double> = mutableListOf()
        FirebaseUtils().fireStoreDatabase.collection("stores")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { storeDocument ->
                    val stockList = storeDocument.get("Stock") as? ArrayList<*>
                    stockList?.forEach { stockMap ->
                        if (stockMap is Map<*, *>) {
                            val productNameFromMap = stockMap["ProductName"]
                            if (productNameFromMap == productName) {
                                val price = stockMap["Price"]
                                priceData.add(price as String)
                            }
                        }
                    }
                }
                for (price in priceData) {
                    try {
                        priceDataToDouble.add(price.toDouble())
                    }
                    catch(e : java.lang.Exception) {
                        Log.e(TAG, "Couldn't read firestore price data. May be null, or contain characters.")
                    }
                }
                callback(priceDataToDouble)
            }
            .addOnFailureListener { exception ->
            }
    }
}