package com.systematicshoppers.sysho.database

/**
 * A data class that stands between the delete function of search and the list to be sent to results.
 * QueryItems that are checked will prevent moving into results until deletions are resolved (with the help of the search UI).
 * They will then be used to update list of items to remove and list of items to send to results.
 */
data class QueryItem(val name: String,
                     var isChecked: Boolean,
                     var quantity: Int)