
package com.systematicshoppers.sysho.fragments

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
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

        //grocery list for selected item
        var storedList: MutableList<String> = mutableListOf("")
        var anyChecked = false
        val deleteList = mutableListOf<CheckBox>()
        var count = 0
        var checkBoxList = mutableListOf<CheckBox>()
        val groceryList = view.findViewById<LinearLayout>(R.id.grocery_list)
        val scroll_view = view.findViewById<ScrollView>(R.id.scroll_view)

        //button to go to results fragment
        shopBtn.setOnClickListener {
            if (!anyChecked) {
                navBar?.isVisible = true
                val fragmentB = ResultsFragment()
                val bundle = Bundle()
                bundle.putStringArrayList("storedList", ArrayList(storedList))
                fragmentB.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .commit()
                view.findNavController().navigate(R.id.action_searchFragment_to_resultsFragment)
            }
            else {
                for (del in deleteList) {
                    groceryList.removeView(del)
                }
            }
        }

        //stand in "database" for testing autocomplete
        var database: MutableList<String> = mutableListOf("apple", "orange", "banana", "orangutan")

        //create search bar w autofill
        val searchbar = view.findViewById<AutoCompleteTextView>(R.id.search_bar)
        val adapter = activity?.let {
            ArrayAdapter<String>(it, android.R.layout.simple_list_item_1, database)
        }
        searchbar.setAdapter(adapter)

        fun addItemToList(autoCompleteTextView: AutoCompleteTextView) {
            autoCompleteTextView.setOnEditorActionListener { grocery_item, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (searchbar.text.isNotEmpty()) {
                        //need a smaller row layout to have checkbox next to text
                        val rowLayout = LinearLayout(this.context)
                        rowLayout.orientation = LinearLayout.HORIZONTAL

                        //adds new item onto fragment list
                        val checkBox = CheckBox(this.context)
                        checkBox.text = searchbar.text
                        checkBoxList.add(checkBox)
                        count++
                        groceryList.addView(checkBox)

                        //adds new item onto mutable grocery list
                        storedList.add(searchbar.text.toString())

                        //focuses on item added to bottom of list
                        scroll_view.fullScroll(View.FOCUS_DOWN)
                        searchbar.text.clear()
                    }
                    //if anything is checked, change button and change to delete mode
                    for (toDelete in checkBoxList) {
                        toDelete.setOnCheckedChangeListener { _, isChecked ->
                            if (toDelete.isChecked) {
                                anyChecked = true
                                deleteList.add(toDelete)
                                shopBtn.text = "Delete"
                                shopBtn.setBackgroundResource(R.color.red)
                                Toast.makeText(this.context, "Checked!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }


//                    for (toDelete in checkBoxList) {
//                        toDelete.setOnCheckedChangeListener { _, isChecked ->
//                            if (anyChecked) {
//                                shopBtn.text = "Delete"
//                                shopBtn.setBackgroundResource(R.color.red)
//                                //Toast.makeText(this.context, "anyChecked True", Toast.LENGTH_SHORT).show()
//                            }
//                            else {
//                                shopBtn.text = "Ready to Shop"
//                                shopBtn.setBackgroundResource(R.color.green_400_dark)
//                                //Toast.makeText(this.context, "anyChecked False", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }


                    true
                } else {
                    false
                }
            }
        }

        fun deleteItems() {
            for (i in 1..checkBoxList.size) {
                val checkBoxtoDelete = view.findViewById<CheckBox>(i)
                if (checkBoxtoDelete.isChecked) {
                    groceryList.removeView(checkBoxtoDelete)
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
