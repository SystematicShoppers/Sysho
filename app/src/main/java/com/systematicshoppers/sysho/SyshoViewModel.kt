package com.systematicshoppers.sysho

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.systematicshoppers.sysho.database.*

/**
 * A central ViewModel for the entire app to communicate data between Fragments.
 * ViewModels are a special structure where data can be stored instead of sent as args between Fragments.
 * ViewModel data lives with the Activity and can exchange data between fragments of that Activity.
 */
class SyshoViewModel : ViewModel() {

    // Mutable live data properties for various data types
    private var _stores = MutableLiveData<List<Store>>()
    val stores: LiveData<List<Store>> = _stores

    /**
     * Sets the value of the _stores MutableLiveData.
     * @param passStores A list of Store objects.
     */
    fun setStoresListData(passStores: List<Store>) {
        _stores.value = passStores
    }


    /**
     * Sets the value of the _store MutableLiveData.
     * @param passStore A Store object.
     */
    private var _store = MutableLiveData<Store>()
    val store: LiveData<Store> = _store
    fun setStoreData(passStore: Store) {
        _store.value = passStore
    }

    /**
     * Reloads the data of the store from Firestore based on the given documentId.
     * @param documentId The document ID of the store in the Firebase Firestore collection.
     */
    fun reloadStoreData(documentId: String?) {
        if (documentId != null) {
            FirebaseUtils().fireStoreDatabase.collection("stores").document(documentId)
                .get()
                .addOnSuccessListener { document ->
                    val store = document.toObject(Store::class.java)
                    if (store != null) {
                        setStoreData(store)
                    }
                }
        }
    }

    /**
     * Sets the value of the _product MutableLiveData.
     * @param passProduct A Product object.
     */
    private var _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product
    fun setProductData(passProduct: Product) {
        _product.value = passProduct
    }

    /**
     * Sets the value of the _products MutableLiveData.
     * @param passProducts A list of Product objects.
     */
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

    /**ResultsFragment callbacks**/
    /**
     * Sets the value of the _loadCoordinatesCallback MutableLiveData.
     * @param result A Boolean value to indicate the result of loading coordinates.
     */
    private val _loadCoordinatesCallback = MutableLiveData<Boolean>()
    val loadCoordinatesCallback: LiveData<Boolean> = _loadCoordinatesCallback
    fun loadCoordinatesCallback(result: Boolean) {
        _loadCoordinatesCallback.value = result
    }

    /**
     * Sets the value of the _totalPriceCallback MutableLiveData and updates the totalPrice MutableLiveData.
     * @param result A Boolean value to indicate the result of calculating the total price.
     * @param total The calculated total price.
     */
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
    /**
     * Boolean toggles to trigger observer. Simple function to reset adapter
     *
     * Sets the value of the _apiStoreAdapterNotice MutableLiveData.
     * @param passNotice A Boolean value to indicate the adapter notice.
     */
    private val _apiStoreAdapterNotice = MutableLiveData<Boolean>()
    val apiStoreAdapterNotice: LiveData<Boolean> = _apiStoreAdapterNotice
    @Suppress("RedundantIf")
    fun notifyApiStoreAdapter(passNotice: Boolean) {
        _apiStoreAdapterNotice.value = passNotice
    }

    /** Navigation **/
    /**
     * Sets the value of the _currentFragment MutableLiveData.
     * @param passCurrentFragment A String value indicating the current fragment.
     */
    private val _currentFragment = MutableLiveData<String>()
    val currentFragment: LiveData<String> = _currentFragment
    fun setCurrentFragment(passCurrentFragment: String) {
        _currentFragment.value = passCurrentFragment
        println(_currentFragment.value)
    }
}