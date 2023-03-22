package com.systematicshoppers.sysho.database

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Product(
    @get:PropertyName("ID") @set:PropertyName("ID") var id : String ? = null,
    @get:PropertyName("Price") @set:PropertyName("Price") var price : String ? = null,
    @get:PropertyName("ProductName") @set:PropertyName("ProductName") var productName : String ? = null,
    @DocumentId val DocumentId: String ? = null
)