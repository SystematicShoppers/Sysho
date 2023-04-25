package com.systematicshoppers.sysho.database

import com.google.firebase.firestore.PropertyName
/**An object that packages lat and long values from a Store into an object usable by Google Maps and Geocoder.**/
data class Coordinates (
    @get:PropertyName("Latitude") @set:PropertyName("Latitude") var latitude : Double ? = null,
    @get:PropertyName("Longitude") @set:PropertyName("Longitude") var longitude : Double ? = null
    ) : java.io.Serializable