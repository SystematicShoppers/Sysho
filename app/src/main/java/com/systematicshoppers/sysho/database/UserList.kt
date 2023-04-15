package com.systematicshoppers.sysho.database

data class UserList(
    var id: String = "",
    var items: List<ListItem> = mutableListOf()
) : java.io.Serializable
