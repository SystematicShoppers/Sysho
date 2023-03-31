
package com.systematicshoppers.sysho.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.systematicshoppers.sysho.R
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.adapters.QueryListAdapter
import com.systematicshoppers.sysho.database.QueryItem

class SearchFragment : Fragment(), QueryListAdapter.ClickListener {

    private val viewModel: SyshoViewModel by activityViewModels()
    private val checkBoxList: MutableList<CheckBox> = mutableListOf()
    private val deleteList: MutableList<QueryItem> = mutableListOf()
    private val queryList: MutableList<QueryItem> = mutableListOf()
    private var anyChecked = false
    private var autoCompleteList: List<String> = listOf()
    private lateinit var searchbar: AutoCompleteTextView
    private lateinit var list: MutableList<String>
    private lateinit var groceryList: LinearLayout
    private lateinit var navBar: BottomNavigationView
    private lateinit var shopBtn: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        navBar = activity?.findViewById(R.id.bottomNavigationView)!!
        shopBtn = view.findViewById(R.id.shopBtn)
        searchbar = view.findViewById(R.id.search_bar)
        list = mutableListOf()
        navBar.isVisible = false
        val searchFragmentRecyclerViewAdapter = QueryListAdapter(queryList, this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.searchFragmentRecyclerView)
        recyclerView.adapter = searchFragmentRecyclerViewAdapter
        val recyclerViewLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        recyclerView.layoutManager = recyclerViewLayoutManager
        shopBtnSetOnClick()


        searchbar.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val query = searchbar.text.toString()
                if(autoCompleteList.contains(query)) {
                    if(queryIsNotListed(query)) {
                        queryList.add(QueryItem(query, false))
                        searchFragmentRecyclerViewAdapter.notifyItemInserted(queryList.size - 1)
                        searchbar.setText("")
                        searchbar.requestFocus()
                    }
                    else {
                        Toast.makeText(requireContext(), "$query is already listed.", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(requireContext(), "$query was not found to be a known product.", Toast.LENGTH_SHORT).show()
                    searchbar.setText("")
                    searchbar.requestFocus()
                }
                true
            } else {
                false
            }
        }




        viewModel.autoComplete.observe(viewLifecycleOwner) { it ->
            autoCompleteList = it
            val adapter = activity?.let {
                  ArrayAdapter(it, android.R.layout.simple_list_item_1, autoCompleteList)
              }
             searchbar.setAdapter(adapter)
        }

        viewModel.queryList.observe(viewLifecycleOwner) { it ->
            println("Observing checked box")
        }
        return view
    }

    private fun shopBtnSetOnClick() {
        shopBtn.setOnClickListener {
            if (!anyChecked) {
                navBar.isVisible = true
                viewModel.setResultsList(list)
                //TODO: update list for viewModel after deletion
                parentFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .commit()
                view?.findNavController()?.navigate(R.id.action_searchFragment_to_resultsFragment)
            }
            else { //the button is in Delete mode
                for (queryToDelete in deleteList) {
                    if(queryList.contains(queryToDelete))
                        queryList.remove(queryToDelete)
                }
                //reset to Ready to shop
                anyChecked = false
                shopBtn.text = getString(R.string.ready_to_shop)
                shopBtn.setBackgroundColor(Color.parseColor("#66bb6a"))
                deleteList.clear()
            }
        }
    }


    private fun queryIsNotListed(query: String): Boolean {
        for(i in queryList.indices) {
            if(query == queryList[i].name)
                return false
        }
        return true
    }

    override fun onCheckBoxClick() {
        onCheckBoxChecked()
    }

    private fun onCheckBoxChecked() {
        // Check if any of the checkboxes are checked
        var somethingIsChecked = false
        for(i in queryList.indices) {
            if(queryList[i].isChecked) {
                somethingIsChecked = true
                deleteList.add(queryList[i])
            }
            if(!queryList[i].isChecked && deleteList.contains(queryList[i]))
                deleteList.remove(queryList[i])
            if(somethingIsChecked) {
                shopBtn.text = "Delete"
                shopBtn.setBackgroundColor(Color.RED)
            }
            else {
                shopBtn.text = getString(R.string.ready_to_shop)
                shopBtn.setBackgroundColor(Color.parseColor("#66bb6a"))
                deleteList.clear()
            }
        }
    }

}
