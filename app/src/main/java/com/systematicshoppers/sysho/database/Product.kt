package com.systematicshoppers.sysho.database

import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

const val TAG = "Product"
data class Product(
    @get:PropertyName("ID") @set:PropertyName("ID") var id : String ? = null,
    @get:PropertyName("Price") @set:PropertyName("Price") var price : String ? = null,
    @get:PropertyName("ProductName") @set:PropertyName("ProductName") var productName : String ? = null,
    @DocumentId val DocumentId: String ? = null
) {
    fun mapToProduct(map: MutableMap<String, Any>?): Product {
        var mId: String? = null
        var mPrice: String? = null
        var mProductName: String? = null
        try {
            mId = map?.get("ID").toString()
        } catch (e: java.lang.Exception) {
            Log.v(TAG, "Data does not include key 'ID'. Could not convert map to Product")
        }
        try {
            mPrice = map?.get("Price").toString()
        } catch (e: java.lang.Exception) {
            Log.v(TAG, "Data does not include key 'Price'. Could not convert map to Product")
        }
        try {
            mProductName = map?.get("ProductName").toString()
        } catch (e: java.lang.Exception) {
            Log.v(TAG, "Data does not include key 'ProductName'. Could not convert map to Product")
        }
        return Product(mId, mPrice, mProductName)
    }
}