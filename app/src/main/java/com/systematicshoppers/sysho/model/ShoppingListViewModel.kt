package com.systematicshoppers.sysho.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class ShoppingListViewModel : ViewModel() {
    private val _listToShop = MutableLiveData<String>("") //FIXME currently configured for single values. How to set it up for lists?
    val listToShop: LiveData<String> = _listToShop //FIXME currently configured for single values. How to set it up for lists?

    fun setListToShop(listToShop: String) { //FIXME currently configured for single values. How to set it up for lists?
        _listToShop.value = listToShop //FIXME currently configured for single values. How to set it up for lists?
    }
}