
package com.systematicshoppers.sysho.fragments

import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.view.indices
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.systematicshoppers.sysho.R
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.SearchList
import com.systematicshoppers.sysho.databinding.FragmentSearchBinding
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    //The following code is to display results in the results page
    private val viewModel: SyshoViewModel by activityViewModels()  //used for tranferring data
    private var binding: FragmentSearchBinding? = null                          //used for transferring data
    private var autoCompleteList: List<String> = listOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val shopBtn = view.findViewById<Button>(R.id.shopBtn)
        val groceryList = view.findViewById<LinearLayout>(R.id.grocery_list)
        val scrollView = view.findViewById<ScrollView>(R.id.scroll_view)
        val navBar: BottomNavigationView? = activity?.findViewById(R.id.bottomNavigationView)
        val searchbar = view.findViewById<AutoCompleteTextView>(R.id.search_bar)
        var anyChecked = false
        val deleteList = arrayListOf<CheckBox>()
        val checkBoxList = arrayListOf<CheckBox>()
        val quantityList = arrayListOf<TextView>()
        val list = mutableListOf<String>()

        navBar?.isVisible = false

        //button to go to results fragment
        shopBtn.setOnClickListener {
            if (!anyChecked) {
                navBar?.isVisible = true
                viewModel.setResultsList(list)
                parentFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .commit()
                view.findNavController().navigate(R.id.action_searchFragment_to_resultsFragment)
            }
            else { //the button is in Delete mode
                for (del in deleteList) {
                    if (list.contains(del.text.toString()))
                        list.remove(del.text.toString())
                    groceryList.removeView(del)
                    checkBoxList.remove(del)
                }
                //reset to Ready to shop
                anyChecked = false
                shopBtn.text = getString(R.string.ready_to_shop)
                shopBtn.setBackgroundColor(Color.parseColor("#66bb6a"))
                deleteList.clear()
                //Toast.makeText(this.context, checkBoxList.size.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        var adapter = activity?.let {
            ArrayAdapter(it, android.R.layout.simple_list_item_1, autoCompleteList)
        }
        searchbar.setAdapter(adapter)
        searchbar.onFocusChangeListener







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
                shopBtn.text = getString(R.string.ready_to_shop)
                shopBtn.setBackgroundColor(Color.parseColor("#66bb6a"))
                deleteList.clear()
            }
        }

        fun addItemToList(autoCompleteTextView: AutoCompleteTextView) {
            autoCompleteTextView.setOnEditorActionListener { grocery_item, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (searchbar.text.isNotEmpty()) {
                        //need a smaller row layout to have checkbox + text + quantity
                        val rowLayout = LinearLayout(this.context)
                        rowLayout.orientation = LinearLayout.HORIZONTAL
                        rowLayout.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            120
                        )

                        //adds new item onto row
                        val checkBox = CheckBox(this.context)
                        checkBox.text = searchbar.text
                        list.add(checkBox.text.toString())
                        //adds new item to row and to view
                        checkBoxList.add(checkBox)
                        rowLayout.addView(checkBox)

                        //add buttons and quantity to the a relative view
                        val relativeLayout = RelativeLayout(this.context)
                        val quantityLayout = LinearLayout(this.context)

                        val params = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

                        relativeLayout.addView(quantityLayout, params)
                        //decrease button
                        val decrease = Button(this.context)
                        decrease.text = "-"
                        decrease.textSize = 16.0f
                        decrease.setTextColor(Color.parseColor("#FFFFFFFF"))
                        decrease.setBackgroundColor(Color.parseColor("#338a3e"))
                        quantityLayout.addView(decrease)

                        //quantity
                        var quantity = TextView(this.context)
                        quantity.text = "1"
                        //add quantity to list of quantities
                        quantityList.add(quantity)
                        quantityLayout.addView(quantity)

                        //increase button
                        val increase = Button(this.context)
                        increase.text = "+"
                        increase.setTextColor(Color.parseColor("#FFFFFFFF"))
                        increase.setBackgroundColor(Color.parseColor("#338a3e"))
                        quantityLayout.addView(increase)

                        //smaller buttons, larger area for number
                        decrease.layoutParams = LinearLayout.LayoutParams(
                            100, // set the width to 100px or any other value you need
                            100,
                            0.0f
                        )
                        quantity.layoutParams = LinearLayout.LayoutParams(
                            100, // set the width to 100px or any other value you need
                            100,
                            0.0f
                        )
                        increase.layoutParams = LinearLayout.LayoutParams(
                            100, // set the width to 100px or any other value you need
                            100,
                            0.0f
                        )
                        decrease.gravity = Gravity.CENTER
                        quantity.gravity = Gravity.CENTER
                        increase.gravity = Gravity.CENTER


                        //add quantity to rowLayout
                        rowLayout.addView(relativeLayout)

                        //add row to the grocery list
                        groceryList.addView(rowLayout)


                        decrease.setOnClickListener {
                            val quantInt = quantity.text.toString().toInt()
                            if (quantInt > 1) {
                                quantity.text = (quantInt - 1).toString()
                            }
                        }
                        increase.setOnClickListener {
                            val quantInt = quantity.text.toString().toInt()
                            quantity.text = (quantInt + 1).toString()
                        }

                        //focuses on item added to bottom of list
                        scrollView.fullScroll(View.FOCUS_DOWN)
                        searchbar.text.clear()
                    }
                    //if anything is checked, change button and change to delete mode
                    if (checkBoxList.size == 0) {
                        anyChecked = false
                        shopBtn.text = getString(R.string.ready_to_shop)
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

        viewModel.autoComplete.observe(viewLifecycleOwner) {
            autoCompleteList = it
            adapter = activity?.let {
                ArrayAdapter(it, android.R.layout.simple_list_item_1, autoCompleteList)
            }
            searchbar.setAdapter(adapter)
        }

        return view
    }

}
