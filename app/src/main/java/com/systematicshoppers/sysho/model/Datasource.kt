package com.systematicshoppers.sysho.model

class Datasource {
    fun loadStores(): List<Store> {
        return listOf<Store>(
            Store("Target"),
            Store("Walmart"),
            Store("Ward's"),
            Store("Trader Joe's"),
            Store("Wholefoods"),
            Store("Publix"),
            Store("Earth Origins")
            )
    }
}