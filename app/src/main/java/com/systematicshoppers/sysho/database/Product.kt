package com.systematicshoppers.sysho.database

import com.google.firebase.firestore.DocumentId

data class Product(
    var id : Int ? = null,
    var price : Double ? = null,
    var productName : String ? = null,
    @DocumentId val DocumentId: String ? = null
)