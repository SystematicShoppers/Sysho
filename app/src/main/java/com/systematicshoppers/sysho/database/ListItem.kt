package com.systematicshoppers.sysho.database

import com.google.firebase.firestore.PropertyName

data class ListItem (
    @get:PropertyName("entry") @set:PropertyName("entry") var entry: String = "",
    @get:PropertyName("quantity") @set:PropertyName("quantity") var quantity: Int = 0
        ) : java.io.Serializable