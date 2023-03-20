
package com.systematicshoppers.sysho.fragments

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.activityViewModels
import com.systematicshoppers.sysho.databinding.FragmentSearchBinding
import com.systematicshoppers.sysho.model.ShoppingListViewModel

class SearchFragment : Fragment() {

    //The following code is to display results in the results page
    private val sharedViewModel: ShoppingListViewModel by activityViewModels()  //used for tranferring data
    private var binding: FragmentSearchBinding? = null                          //used for transferring data

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val shopBtn = view.findViewById<Button>(R.id.shopBtn)
        val navBar: BottomNavigationView? = activity?.findViewById(R.id.bottomNavigationView)
        navBar?.isVisible = false

        //grocery list for selected item
        var storedList: ArrayList<String> = arrayListOf()
        var anyChecked = false
        val deleteList = arrayListOf<CheckBox>()
        var checkBoxList = arrayListOf<CheckBox>()
        val groceryList = view.findViewById<LinearLayout>(R.id.grocery_list)
        val scroll_view = view.findViewById<ScrollView>(R.id.scroll_view)

        //button to go to results fragment
        shopBtn.setOnClickListener {
            if (!anyChecked) {
                for (item in checkBoxList) {
                    storedList.add(item.text.toString())
                }
                navBar?.isVisible = true

                //The following code is to display results in the results page
                sharedViewModel.setListToShop(storedList[0])

                parentFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .commit()
                view.findNavController().navigate(R.id.action_searchFragment_to_resultsFragment)
            }
            else { //the button is in Delete mode
                for (del in deleteList) {
                    groceryList.removeView(del)
                    checkBoxList.remove(del)
                }
                //reset to Ready to shop
                anyChecked = false
                shopBtn.text = "Ready to Shop"
                shopBtn.setBackgroundColor(Color.parseColor("#66bb6a"))
                deleteList.clear()
                Toast.makeText(this.context, checkBoxList.size.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        //stand in "database" for testing autocomplete
        var database: MutableList<String> = mutableListOf("apple", "orange", "banana", "orangutan")

        //create search bar w autocomplete
        val searchbar = view.findViewById<AutoCompleteTextView>(R.id.search_bar)
        val adapter = activity?.let {
            ArrayAdapter<String>(it, android.R.layout.simple_list_item_1, database)
        }
        searchbar.setAdapter(adapter)

        fun onCheckBoxChecked(isChecked: Boolean, toDelete: CheckBox) {
            // Check if any of the checkboxes are checked
            anyChecked = checkBoxList.any { it.isChecked }

            // Set the button functionality/appearance based on whether any checkboxes are checked
            if (anyChecked) {
                shopBtn.text = "Delete"
                shopBtn.setBackgroundColor(Color.RED)
                deleteList.clear()
                for (item in checkBoxList) {
                    if (item.isChecked) {
                        deleteList.add(item)
                    }
                }
            }
            else {
                shopBtn.text = "Ready to Shop"
                shopBtn.setBackgroundColor(Color.parseColor("#66bb6a"))
                deleteList.clear()
            }
        }

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

                        //adds new item to list and to view
                        checkBoxList.add(checkBox)
                        groceryList.addView(checkBox)
                        Toast.makeText(this.context, checkBoxList.size.toString(), Toast.LENGTH_SHORT).show()

                        //focuses on item added to bottom of list
                        scroll_view.fullScroll(View.FOCUS_DOWN)
                        searchbar.text.clear()
                    }
                    //if anything is checked, change button and change to delete mode
                    if (checkBoxList.size == 0) {
                        anyChecked = false
                        shopBtn.text = "Ready to Shop"
                        shopBtn.setBackgroundColor(Color.parseColor("#66bb6a"))
                        deleteList.clear()
                    }
                    else {
                        for (toDelete in checkBoxList) {
                            toDelete.setOnCheckedChangeListener { _, isChecked ->
                                onCheckBoxChecked(isChecked, toDelete)
                            }
                        }
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
