//Source of help with View Binding: https://developer.android.com/codelabs/basic-android-kotlin-training-shared-viewmodel#0
package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.databinding.FragmentResultsBinding
import com.systematicshoppers.sysho.model.ShoppingListViewModel

class ResultsFragment : Fragment() {

    private var binding: FragmentResultsBinding? = null

    private val sharedViewModel: ShoppingListViewModel by activityViewModels()

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