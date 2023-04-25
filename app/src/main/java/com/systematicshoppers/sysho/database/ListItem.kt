package com.systematicshoppers.sysho.database

import com.google.firebase.firestore.PropertyName

/**
 * The ListItem data class is used within the UserList class as a list of items.
 *
 * This class represents an individual item in a user's saved shopping list, containing two properties:
 * entry: A string which represents the name of the item
 * quantity: An integer which represents the quantity of the item
 *
 * The @get:PropertyName and @set:PropertyName are used to define custom property names
 * for the entry and quantity properties when they are serialized or deserialized with Firestore.
 * This has to be done to maintain consistency between the property names in the data class
 * and the names used in the Firestore database so that they can be accessed correctly from the code.
 *
 * The ListItemAdapter is used to display these items in a RecyclerView in the SavedListsFragment.
 */
data class ListItem (
    @get:PropertyName("entry") @set:PropertyName("entry") var entry: String = "",
    @get:PropertyName("quantity") @set:PropertyName("quantity") var quantity: Int = 0
        ) : java.io.Serializable