package com.systematicshoppers.sysho.database

import android.location.Address
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.lang.reflect.Type

data class Store(
    @get:PropertyName("Latitude") @set:PropertyName("Latitude") var latitude : String ? = null,
    @get:PropertyName("Longitude") @set:PropertyName("Longitude") var longitude : String ? = null,
    @get:PropertyName("Store") @set:PropertyName("Store") var store : String ? = null,
    @get:PropertyName("StoreID") @set:PropertyName("StoreID") var storeId : String ? = null,
   // @get:PropertyName("Products") @set:PropertyName("Products") var products : List<Int> ? = null,
    @get:PropertyName("Stock") @set:PropertyName("Stock") var stock : List<MutableMap<String,Any>> ? = null,
    var address: Address? = null,
    @DocumentId val DocumentId: String ? = null
)
