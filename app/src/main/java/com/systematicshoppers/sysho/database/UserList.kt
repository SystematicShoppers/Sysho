package com.systematicshoppers.sysho.database

/**
 * UserList is a data class that represents a user's shopping list. It has two properties:
 * id: A String representing the unique identifier of the list. It is initialized with an empty string.
 * items: A List of ListItem objects representing the individual items in the shopping list. It is initialized with an empty list by default.
 *
 * UserList implements the Serializable interface so that it can be passed between activities.
 */
data class UserList(
    var id: String = "",
    val items: List<ListItem> = emptyList()
) : java.io.Serializable
