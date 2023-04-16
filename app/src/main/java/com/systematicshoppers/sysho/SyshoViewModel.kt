package com.systematicshoppers.sysho

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.systematicshoppers.sysho.database.*

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

    private val _queryList = MutableLiveData<MutableList<QueryItem>>()
    val queryList: LiveData<MutableList<QueryItem>> = _queryList
    fun updateQueryList(passQueryList: MutableList<QueryItem>) {
        _queryList.value = passQueryList
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

    private val _resultsList = MutableLiveData<List<ListItem>>()
    val resultsList: LiveData<List<ListItem>> = _resultsList
    fun setResultsList(passResultsList: List<ListItem>) {
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
    fun totalPriceCallback(result: Boolean, total: Double) {
        setTotalPrice(total)
        _totalPriceCallback.value = result
    }

    /** Settings **/
    private val _distanceFilter = MutableLiveData<Double>()
    val distanceFilter: LiveData<Double> = _distanceFilter
    fun setDistanceFilter(passDistanceFilter: Double) {
        _distanceFilter.value = passDistanceFilter
    }

    /** Api Dialogs **/
    private val _salePercent = MutableLiveData<Double>()
    val salePercent: LiveData<Double> = _salePercent
    fun setSalePercent(passSalePercent: Double) {
        _salePercent.value = passSalePercent
    }

    /** Api Updates **/
    /** Boolean toggles to trigger observer. Simple function to reset adapter. **/
    private val _apiStoreAdapterNotice = MutableLiveData<Boolean>()
    val apiStoreAdapterNotice: LiveData<Boolean> = _apiStoreAdapterNotice
    @Suppress("RedundantIf")
    fun notifyApiStoreAdapter(passNotice: Boolean) {
        _apiStoreAdapterNotice.value = passNotice
    }

    /** Navigation **/
    private val _currentFragment = MutableLiveData<String>()
    val currentFragment: LiveData<String> = _currentFragment
    fun setCurrentFragment(passCurrentFragment: String) {
        _currentFragment.value = passCurrentFragment
        println(_currentFragment.value)
    }
}