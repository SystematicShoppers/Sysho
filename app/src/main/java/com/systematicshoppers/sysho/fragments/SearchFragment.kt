
package com.systematicshoppers.sysho.fragments

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        var database: MutableList<String> = mutableListOf("apple", "orange", "banana")

        //autofill for search bar
        val adapter = activity?.let {
            ArrayAdapter<String>(it, android.R.layout.simple_list_item_1, database)
        }
        val editText = view.findViewById<View>(R.id.search_bar) as AutoCompleteTextView
        editText.setAdapter(adapter)

        //grocery list for selected item
        var groceryList: MutableList<String> = mutableListOf("apple")
        var index = 0

        editText.setOnClickListener {
            fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event?.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    val item = view.findViewById<TextView>(R.id.item1)
                    view.findViewById<TextView>(R.id.grocerylist).text = "Hello"
                    if (editText.text.isNotEmpty()) {
                        //add item to mutable list
//                        val entry: String = editText.text.toString()
//                        groceryList.add(index, entry)
//                        index++


                        //update the items with the input item
                        item.text = editText.text.toString()
                        editText.text.clear()
                    }
                }
                return true
            }
        }

        return view
    }
}
