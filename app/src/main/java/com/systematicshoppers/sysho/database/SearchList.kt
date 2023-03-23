package com.systematicshoppers.sysho.database

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class SearchList(
    @get:PropertyName("searchList") @set:PropertyName("searchList") var searchList : ArrayList<String> ? = null,
    @DocumentId val DocumentId: String ? = null
)

