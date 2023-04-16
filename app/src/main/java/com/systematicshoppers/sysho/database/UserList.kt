package com.systematicshoppers.sysho.database

data class UserList(
    var id: String = "",
    val items: List<ListItem> = emptyList()
) : java.io.Serializable
