
package com.systematicshoppers.sysho.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.adapters.QueryListAdapter
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.ListItem
import com.systematicshoppers.sysho.database.QueryItem

class SearchFragment : Fragment(), QueryListAdapter.ClickListener {

    private val deleteList: MutableList<QueryItem> = mutableListOf()
    val queryList: MutableList<QueryItem> = mutableListOf()
    private val viewModel: SyshoViewModel by activityViewModels()
    private var autoCompleteList: List<String> = listOf()
    private lateinit var searchFragmentRecyclerViewAdapter: QueryListAdapter
    private lateinit var searchbar: AutoCompleteTextView
    private lateinit var list: MutableList<ListItem>
    private lateinit var shopBtn: Button



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.searchFragmentRecyclerView)
        list = mutableListOf()
        shopBtn = view.findViewById(R.id.shopBtn)
        searchbar = view.findViewById(R.id.search_bar)
        searchFragmentRecyclerViewAdapter = QueryListAdapter(queryList, this)

        /**
         * This is the setup for the recyclerview display. This replaced the old ScrollView
         * because there was no cap on the amount of items a list can contain.
         *
         * **/

        recyclerView.adapter = searchFragmentRecyclerViewAdapter
        val recyclerViewLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        recyclerView.layoutManager = recyclerViewLayoutManager


        /**
         *
         * This key listener takes in either hardware key 13 (ENTER) or ENTER on the Android soft keyboard
         * and builds QueryItems based on the text field. QueryItem is a custom object in the database package
         * with two fields: a name (String) and isChecked (Boolean). isChecked is used in the recyclerview adapter
         * and shopbtn to control states of the fragments UI. The listener will also handle logic, throwing any
         * entry that is not an auto complete entry or is already listed. Anytime ENTER is triggered, focus is
         * brought back to the searchbar.
         *
         * **/

        searchbar.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val query = searchbar.text.toString()
                if(autoCompleteList.contains(query)) {
                    if(queryIsNotListed(query)) {
                        queryList.add(QueryItem(query, false, 1))
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

        /**
         * The shop button can be manipulated in two ways: one from this click listener,
         * and another from the QueryListAdapter.ClickListener. If the QLA listener is triggered,
         * the call goes through a wrapper onCheckBoxList() to hand over logic to the fragment at
         * onCheckBoxChecked(). Here the button will change state between ready/delete if any
         * box is checked. The shopBtn listener has the following logic:
         *
         * 1. check to see if any boxes are checked.
         * 2. if nothing is checked, clear 'list' then update 'list' and send it to the viewmodel. This will search firebase
         *    based on name and retrieve those products in the ResultsFragment. Then transition.
         * 3. if something is checked it will call the adapter to remove any checked items, change
         *    back to a ready state.
         *
         * **/

        shopBtn.setOnClickListener {
            var somethingIsChecked = false
            for(i in queryList.indices) {
                if (queryList[i].isChecked)
                    somethingIsChecked = true
            }
            if (!somethingIsChecked) {
                list = getProductList(queryList, list)
                viewModel.setResultsList(list)
                saveUserList() // Save the user's list to Firebase
                parentFragmentManager.beginTransaction()
                    .replace(R.id.content, ResultsFragment())
                    .addToBackStack(null)
                    .commit()

            }
            else { //the button is in Delete mode
                searchFragmentRecyclerViewAdapter.removeItems(deleteList)
                shopBtn.text = getString(R.string.ready_to_shop)
                shopBtn.setBackgroundColor(Color.parseColor("#66bb6a"))
                deleteList.clear()
            }
        }


        /**
         *
         * The viewmodel here is taking all data from firebase's product database and creating an
         * autocomplete list from it. Because firebase is async (see documentation for Firebase Promises),
         * this list may not appear if the user opens the app and tries to enter information quickly. It
         * needs 1 - 2 seconds to load.
         *
         * **/

        viewModel.autoComplete.observe(viewLifecycleOwner) { it ->
            autoCompleteList = it
            val adapter = activity?.let {
                  ArrayAdapter(it, android.R.layout.simple_list_item_1, autoCompleteList)
              }
             searchbar.setAdapter(adapter)
        }

        return view
    }


    fun queryIsNotListed(query: String): Boolean {
        for(i in queryList.indices) {
            if(query == queryList[i].name)
                return false
        }
        return true
    }

    //wrapper
    override fun onCheckBoxClick() {
        onCheckBoxChecked()
    }

    override fun onMinusClick(quantity: Int, product: String): String {
        var newQuantity = quantity
        if(quantity > 0) {
            newQuantity -= 1
            for(item in queryList) {
                if(product == item.name)
                    item.quantity = newQuantity
            }
        }
        return newQuantity.toString()
    }

    override fun onPlusClick(quantity: Int, product: String): String {
        var newQuantity = quantity
        if(quantity < 99) {
            newQuantity += 1
            for(item in queryList) {
                if(product == item.name)
                    item.quantity = newQuantity
            }
        }
        return newQuantity.toString()
    }

    private fun onCheckBoxChecked() {
        var somethingIsChecked = false
        for(i in queryList.indices) {
            if(queryList[i].isChecked) {
                somethingIsChecked = true
                deleteList.add(queryList[i])
            }
            if(!queryList[i].isChecked && deleteList.contains(queryList[i]))
                deleteList.remove(queryList[i])
            if(somethingIsChecked) {
                shopBtn.text = getString(R.string.delete_btn)
                shopBtn.setBackgroundColor(Color.RED)
            }
            else {
                shopBtn.text = getString(R.string.ready_to_shop)
                shopBtn.setBackgroundColor(Color.parseColor("#66bb6a"))
            }
        }
    }

    fun getProductList(queryList: MutableList<QueryItem>, list: MutableList<ListItem>): MutableList<ListItem> {
        if (list.isEmpty()) {
            // Resize list to match the size of queryList
            list.addAll(List(queryList.size) { ListItem() })
            for (i in queryList.indices) {
                list[i].entry = queryList[i].name
                list[i].quantity = queryList[i].quantity
            }
        } else {
            // Clear list and resize it
            list.clear()
            list.addAll(List(queryList.size) { ListItem() })
            for (i in queryList.indices) {
                list[i].entry = queryList[i].name
                list[i].quantity = queryList[i].quantity
            }
        }
        return list
    }

    private fun saveUserList() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val userListsRef = FirebaseUtils().fireStoreDatabase.collection("Users").document(userId).collection("lists")

            // Generate a unique document ID for the new list
            val newListRef = userListsRef.document()

            // Save the new list under the generated document ID
            newListRef.set(mapOf("items" to list))
        }
    }

}
