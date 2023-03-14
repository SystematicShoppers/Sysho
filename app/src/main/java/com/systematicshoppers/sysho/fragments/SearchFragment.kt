
package com.systematicshoppers.sysho.fragments

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
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
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.Product
import com.systematicshoppers.sysho.database.Store
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

        val productData = readProductData()
        productData
        //autofill for search bar
        val adapter = activity?.let {
         //   ArrayAdapter<String>(it, android.R.layout.simple_list_item_1, productData)
        }
        val editText = view.findViewById<View>(R.id.searchBar) as AutoCompleteTextView
       // editText.setAdapter(adapter)

        //grocery list for selected item
        var groceryList: MutableList<String> = mutableListOf("apple")
        var index = 0

        editText.setOnClickListener {

        }
        return view
    }

    private fun readProductData() : MutableList<Product>? {
        val productData = mutableListOf<Product>()
        FirebaseUtils().fireStoreDatabase.collection("Products")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { document ->
                    Log.d(ContentValues.TAG, "Read document with ID ${document.id}")
                    productData.add(document.toObject(Product::class.java))
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents $exception")
            }
        return productData
    }


}
