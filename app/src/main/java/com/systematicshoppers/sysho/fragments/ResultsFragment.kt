
package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.systematicshoppers.sysho.R

class ResultsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_results, container, false)

        val linearLayout = view.findViewById<LinearLayout>(R.id.repeatList)
        val data = arguments?.getStringArrayList("storedList")?.toMutableList()
        if (data != null) {
            for (item in data) {
                val textView = TextView(this.context)
                textView.text = item
                textView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                linearLayout.addView(textView)
            }
        }


        return view
    }
}