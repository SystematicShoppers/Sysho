//Source of help with View Binding: https://developer.android.com/codelabs/basic-android-kotlin-training-shared-viewmodel#0
package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.databinding.FragmentResultsBinding

class ResultsFragment : Fragment() {

    private var binding: FragmentResultsBinding? = null

    private val sharedViewModel: SyshoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // The following code is to display search result in results page:
        val fragmentBinding = FragmentResultsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            //The following code is to display results in the results page
            viewModel = sharedViewModel
        }
    }
}