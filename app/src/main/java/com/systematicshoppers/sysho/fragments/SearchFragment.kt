
package com.systematicshoppers.sysho.fragments

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.activities.MainActivity
import org.w3c.dom.Text

class SearchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val shopBtn = view.findViewById<Button>(R.id.shopBtn)
        val navBar: BottomNavigationView? = activity?.findViewById(R.id.bottomNavigationView)
        navBar?.isVisible = false

        shopBtn.setOnClickListener {
            navBar?.isVisible = true
            view.findNavController().navigate(R.id.action_searchFragment_to_resultsFragment)
        }

        //stand in "database" for testing
        var database: MutableList<String> = mutableListOf("apple", "orange", "banana", "orangutan")

        //autofill for search bar
        val adapter = activity?.let {
            ArrayAdapter<String>(it, android.R.layout.simple_list_item_1, database)
        }

        val searchbar = view.findViewById<AutoCompleteTextView>(R.id.search_bar)
        searchbar.setAdapter(adapter)

        //grocery list for selected item
        var groceryList: MutableList<String> = mutableListOf("apple")


        fun addItemToList(autoCompleteTextView: AutoCompleteTextView) {
            autoCompleteTextView.setOnEditorActionListener { textView, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val item = view.findViewById<TextView>(R.id.item1)
                    if (searchbar.text.isNotEmpty()) {
                        //add item to mutable list
//                        val entry: String = editText.text.toString()
//                        groceryList.add(index, entry)
//                        index++

                        //update the items with the input item
                        item.text = searchbar.text.toString()
                        searchbar.text.clear()
                    }
                    true
                } else {
                    false
                }
            }
        }



        //when clicking, to do with search bar
        searchbar.setOnClickListener {
            addItemToList(searchbar)
        }

        return view
    }
}
