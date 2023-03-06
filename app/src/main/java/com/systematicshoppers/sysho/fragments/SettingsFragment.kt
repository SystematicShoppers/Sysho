
package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.systematicshoppers.sysho.R


class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val distanceSeekBar: SeekBar = view.findViewById(R.id.distance_seekbar)
        val distanceTextView: TextView = view.findViewById(R.id.distance_textview)

        distanceSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val distanceInMiles = progress /1.0
                val distanceText = String.format("%.1f miles", distanceInMiles)
                distanceTextView.text = distanceText
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })


        return view
    }
}