package com.systematicshoppers.sysho

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.systematicshoppers.sysho.database.Product
import com.systematicshoppers.sysho.database.Store

class SyshoViewModel : ViewModel() {

    private var _stores = MutableLiveData<List<Store>>()
    val stores: LiveData<List<Store>> = _stores
    fun setStoreListData(passStores: List<Store>) {
        _stores.value = passStores
    }

    private var _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    fun setProductListData(passProducts: List<Product>) {
        _products.value = passProducts
    }

    private val _listToShop = MutableLiveData<List<String>>()
    val listToShop: LiveData<List<String>> = _listToShop
    fun setListToShop(passListToShop: List<String>) {
        _listToShop.value = passListToShop
    }

    private val _autoComplete = MutableLiveData<List<String>>()
    val autoComplete: LiveData<List<String>> = _autoComplete
    fun setAutoComplete(passAutoComplete: List<String>) {
        _autoComplete.value = passAutoComplete
    }


}