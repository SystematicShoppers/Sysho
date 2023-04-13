package com.systematicshoppers.sysho.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.toObject
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.adapters.ApiProductsAdapter
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.Product

class ApiProductFragment : Fragment(), ApiProductsAdapter.ClickListener {

    private lateinit var apiProductsRecyclerView: RecyclerView
    private lateinit var apiProductsAdapter: ApiProductsAdapter
    private val viewModel: SyshoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_api_product, container, false)


        FirebaseUtils().fireStoreDatabase.collection("products")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val products = mutableListOf<Product>()
                querySnapshot.forEach { productDocument ->
                    Log.d(TAG, "Read document with ID ${productDocument.id}")
                    products.add(productDocument.toObject(Product::class.java))
                }

                apiProductsAdapter = ApiProductsAdapter(requireContext(), products, this)
                val apiProductsLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
                apiProductsRecyclerView = view.findViewById((R.id.api_products_recycler_view))
                apiProductsRecyclerView.adapter = apiProductsAdapter
                apiProductsRecyclerView.layoutManager = apiProductsLayoutManager
            }

        return view
    }

    override fun gotoProduct(position: Int, productData: Product) {
        viewModel.setProductData(productData)
        parentFragmentManager.beginTransaction()
            .replace(R.id.content, ApiProductSelectFragment())
            .addToBackStack(null)
            .commit()
    }
}