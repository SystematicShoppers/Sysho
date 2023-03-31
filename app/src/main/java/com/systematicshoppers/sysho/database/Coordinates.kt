package com.systematicshoppers.sysho.database

import com.google.firebase.firestore.PropertyName

data class Coordinates (
    @get:PropertyName("Latitude") @set:PropertyName("Latitude") var latitude : Double ? = null,
    @get:PropertyName("Longitude") @set:PropertyName("Longitude") var longitude : Double ? = null
    )