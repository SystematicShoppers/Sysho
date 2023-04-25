
package com.systematicshoppers.sysho.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.activities.ApiActivity


class SettingsFragment : Fragment() {

    private val viewModel: SyshoViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val distanceSeekBar: SeekBar = view.findViewById(R.id.distance_seekbar)
        val distanceTextView: TextView = view.findViewById(R.id.distance_textview)
        val accessApiBtn: Button = view.findViewById(R.id.accessApiBtn)

        accessApiBtn.setOnClickListener {
            val intent = Intent(activity, ApiActivity::class.java)
            activity?.startActivity(intent)
        }

        distanceSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val distanceInMiles = progress / 1.0
                viewModel.setDistanceFilter(distanceInMiles)
                val distanceText = String.format("%.1f miles", distanceInMiles)
                distanceTextView.text = distanceText
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

        })

        distanceSeekBar.progress = viewModel.distanceFilter.value!!.toInt() // Set initial progress to viewModel.distanceFilter.value

        // Overload the back button press to reset the view to the search fragment
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val transaction = parentFragmentManager.beginTransaction()      // Begin a fragment transaction
                transaction.replace(R.id.content, ResultsFragment()).commit()   // Replace the current fragment with a new instance of ResultsFragment
            }
        }

        // Add the callback to the onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return view
    }
}