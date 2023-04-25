package com.systematicshoppers.sysho.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.adapters.ApiProductsAdapter
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.Product

/**
 * Fragment for ApiActivity to display the products from the Firestore database
 * and handle the click events of its RecyclerView.
 */
class ApiProductFragment : Fragment(), ApiProductsAdapter.ClickListener {

    private lateinit var apiProductsRecyclerView: RecyclerView
    private lateinit var apiProductsAdapter: ApiProductsAdapter
    private val viewModel: SyshoViewModel by activityViewModels()

    /**
     * Inflate the layout and set up the RecyclerView with its adapter and layout manager.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_api_product, container, false)

        // Retrieve the list of products from Firebase, and initialize the RecyclerView and its adapter.
        getProducts { products ->
            if(isAdded) {
                apiProductsAdapter = ApiProductsAdapter(requireContext(), products, this)
                val apiProductsLayoutManager =
                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                apiProductsRecyclerView = view.findViewById((R.id.api_products_recycler_view))
                apiProductsRecyclerView.adapter = apiProductsAdapter
                apiProductsRecyclerView.layoutManager = apiProductsLayoutManager
            }
        }
        return view
    }

    /**
     * Handle the click event for a product item in the RecyclerView.
     */
    override fun gotoProduct(position: Int, productData: Product) {
        // Set the selected product data in the ViewModel.
        viewModel.setProductData(productData)
        // Replace the current fragment with ApiProductSelectFragment.
        parentFragmentManager.beginTransaction()
            .replace(R.id.content, ApiProductSelectFragment())
            .addToBackStack(null)
            .commit()
    }

    /**
     * Returns through a callback with a list of Products parsed from Firebase documents.
     */
    private fun getProducts(callback: (MutableList<Product>) -> Unit) {
        FirebaseUtils().fireStoreDatabase.collection("products")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val products = mutableListOf<Product>()
                // Iterate through the documents in the querySnapshot and add them to the products list.
                querySnapshot.forEach { productDocument ->
                    Log.d(TAG, "Read document with ID ${productDocument.id}")
                    products.add(productDocument.toObject(Product::class.java))
                }
                // Invoke the callback function with the retrieved products list.
                callback(products)
            }
    }
}