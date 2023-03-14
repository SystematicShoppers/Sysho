package com.systematicshoppers.sysho.activities

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.Product
import com.systematicshoppers.sysho.database.Store
import com.systematicshoppers.sysho.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: SyshoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //FireBase
        val db = Firebase.firestore

        //View binding
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        //Navigation graph (jetpack)
        //The navigation graph has search fragment as home location
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.content) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        //Bottom navigation
        //Bottom navigation is set to use the navigation graph in nav_menu.xml
        val nav = binding.bottomNavigationView
        nav.setupWithNavController(navController)

        /** DO NOT UNCOMMENT **/
        //initData()
        /** DO NOT RUN THIS OR USE THIS FUNCTION **/



       //readData()


    }


    private fun initData() {
        var productDocumentID: String?
        FirebaseUtils().fireStoreDatabase.collection("Stores")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { storeDocument ->
                    Log.d(TAG, "Read document with ID ${storeDocument.id}")
                    var storeProductsIDList : List<*>? = null
                    try {
                        storeProductsIDList = storeDocument.get("Products") as List<*>?
                    }
                    catch(e: Exception) {
                        Log.w(TAG, "Error getting store product list.")
                    }
                    storeProductsIDList

                    if (storeProductsIDList != null) {
                        var doc: DocumentReference?
                        for(id in storeProductsIDList) {
                            try {
                                productDocumentID = id.toString()
                                FirebaseUtils().fireStoreDatabase.collection("Products").whereEqualTo("ID", productDocumentID)
                                    .get()
                                    .addOnSuccessListener { queryDocument ->
                                        queryDocument.forEach { productDocument ->
                                            val product =
                                                productDocument.toObject(Product::class.java)
                                            if (product != null) {
                                                FirebaseUtils().fireStoreDatabase.collection("Stores")
                                                    .document(storeDocument.id).collection("Stock")
                                                    .add(product)
                                            }
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w(TAG, "Could not map product in database to Product object $exception")
                                    }

                            }
                            catch(e: Exception) {
                                Log.w(TAG, "Product not found in Products database.")
                            }
                        }
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents $exception")
            }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun readData() {
        val storesData = mutableListOf<Store>()
        FirebaseUtils().fireStoreDatabase.collection("Stores")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { document ->
                    Log.d(TAG, "Read document with ID ${document.id}")
                    storesData.add(document.toObject(Store::class.java))
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents $exception")
            }
    }
}