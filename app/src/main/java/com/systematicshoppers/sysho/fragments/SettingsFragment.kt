
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

/**
 * The SettingsFragment class is a Fragment that allows users to adjust the search distance filter and also access the API activity.
 */
class SettingsFragment : Fragment() {

    // Obtain a reference to the shared ViewModel instance
    private val viewModel: SyshoViewModel by activityViewModels()
    // Function to create and return the view hierarchy associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment_settings layout and store the resulting view in a variable
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize views in the layout
        val distanceSeekBar: SeekBar = view.findViewById(R.id.distance_seekbar)
        val distanceTextView: TextView = view.findViewById(R.id.distance_textview)
        val accessApiBtn: Button = view.findViewById(R.id.accessApiBtn)

        // Set a click listener for the accessApiBtn button
        accessApiBtn.setOnClickListener {
            // Create an Intent to launch the ApiActivity
            val intent = Intent(activity, ApiActivity::class.java)
            // Start the ApiActivity
            activity?.startActivity(intent)
        }

        // Set a listener for changes on the distanceSeekBar
        distanceSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val distanceInMiles = progress / 1.0                            // Calculate the distance in miles based on the SeekBar progress
                viewModel.setDistanceFilter(distanceInMiles)                    // Update the distance filter in the shared ViewModel
                val distanceText = String.format("%.1f miles", distanceInMiles) // Update the distanceTextView with the new distance value
                distanceTextView.text = distanceText
            }

            // Required to implement the OnSeekBarChangeListener interface
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

        })

        // Set initial progress to viewModel.distanceFilter.value
        distanceSeekBar.progress = viewModel.distanceFilter.value!!.toInt()

        // Handle back button press to reset the view to the ResultsFragment
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val transaction = parentFragmentManager.beginTransaction()      // Begin a fragment transaction
                transaction.replace(R.id.content, ResultsFragment()).commit()   // Replace the current fragment with a new instance of ResultsFragment
            }
        }

        // Add the callback to the onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Return the created view
        return view
    }
}