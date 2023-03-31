package com.systematicshoppers.sysho

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.Product
import com.systematicshoppers.sysho.database.Store
import com.systematicshoppers.sysho.database.TAG

class SyshoViewModel : ViewModel() {

    private var _stores = MutableLiveData<List<Store>>()
    val stores: LiveData<List<Store>> = _stores
    fun setStoresListData(passStores: List<Store>) {
        _stores.value = passStores
    }

    private var _store = MutableLiveData<Store>()
    val store: LiveData<Store> = _store
    fun setStoreData(passStore: Store) {
        _store.value = passStore
    }

    private var _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product
    fun setProductData(passProduct: Product) {
        _product.value = passProduct
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

    private val _dialogEditText = MutableLiveData<String>()
    val dialogEditText: LiveData<String> = _dialogEditText
    fun setDialogEditText(passEditText: String) {
        _dialogEditText.value = passEditText
    }

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice
    fun setTotalPrice(passTotalPrice: Double) {
        _totalPrice.value = passTotalPrice
    }

    fun getTotalPrice(items: List<String>): Double {
        var total: Double = 0.0
        for(i in items.indices) {
            try {
                FirebaseUtils().fireStoreDatabase.collection("products").document(items[i])
                    .get()
                    .addOnSuccessListener { document ->
                        val product = document.toObject(Product::class.java)
                        val itemPrice = product?.price?.toDouble()
                        if (itemPrice != null)
                            total += itemPrice
                    }
                totalPriceCallback(true)
            } catch(e: Exception) {
                Log.v(TAG, "Could not retrieve item on list from Firebase. Price of ${items[i]} was not included in total.")
                totalPriceCallback(false)
            }
        }
        return total
    }

    private val _resultsList = MutableLiveData<List<String>>()
    val resultsList: LiveData<List<String>> = _resultsList
    fun setResultsList(passResultsList: List<String>) {
        _resultsList.value = passResultsList
    }

    private val _currentLong = MutableLiveData<Double>()
    val currentLong: LiveData<Double> = _currentLong
    fun setCurrentLong(passLong: Double) {
        _currentLong.value = passLong
    }
    private val _currentLat = MutableLiveData<Double>()
    val currentLat: LiveData<Double> = _currentLat
    fun setCurrentLat(passLat: Double) {
        _currentLat.value = passLat
    }

    /**ResultsFragment callbacks **/
    private val _loadCoordinatesCallback = MutableLiveData<Boolean>()
    val loadCoordinatesCallback: LiveData<Boolean> = _loadCoordinatesCallback
    fun loadCoordinatesCallback(result: Boolean) {
        _loadCoordinatesCallback.value = result
    }

    private val _totalPriceCallback = MutableLiveData<Boolean>()
    val totalPriceCallback: LiveData<Boolean> = _totalPriceCallback
    fun totalPriceCallback(result: Boolean) {
        _totalPriceCallback.value = result
    }

}