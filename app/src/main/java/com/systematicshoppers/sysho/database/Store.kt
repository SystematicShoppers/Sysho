package com.systematicshoppers.sysho.database

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

/**
 * get/set properties are coming from Firebase.
 * Address, Distance, and Total Price are all part of the Store
 * so that useful display data can be bundled into a RecyclerView
 * **/
data class Store(
    @get:PropertyName("Latitude") @set:PropertyName("Latitude") var latitude : String ? = null,
    @get:PropertyName("Longitude") @set:PropertyName("Longitude") var longitude : String ? = null,
    @get:PropertyName("Store") @set:PropertyName("Store") var store : String ? = null,
    @get:PropertyName("StoreID") @set:PropertyName("StoreID") var storeId : String ? = null,
    @get:PropertyName("Stock") @set:PropertyName("Stock") var stock : List<MutableMap<String,Any>> ? = null,
    var address: String? = null,
    var distance: Double? = null,
    var totalPrice: Double? = null,
    @DocumentId val DocumentId: String ? = null
)
