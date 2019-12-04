package com.ishwal.beverageinventory.ui.addinventory

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ishwal.beverageinventory.utils.Event

class AddInventoryItemViewModel : ViewModel(),
    Observable {
    companion object {
        val TAG = AddInventoryItemViewModel::class.java.simpleName
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    private var _barcodeDataAvailable: MutableLiveData<Event<String>> = MutableLiveData()

     lateinit var itemId : String

    val barcodeDataObserver: LiveData<Event<String>>
        get() = _barcodeDataAvailable

    var productName: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.productName, null)
        }

    var productType: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.productType, null)
        }

    var productBarcode: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.productBarcode, null)
        }

    var productVolume: String = "0"
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.productVolume, null)
        }

    var productVolumeUnit: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.productVolumeUnit, null)
        }

    var quantityCount: Int = 0
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.quantityCount, null)
        }

    var isNewItem: Boolean = true
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.newItem, null)
        }

    fun increaseQuantityCount() {
        this.quantityCount = this.quantityCount + 1
    }

    fun decreaseQuantityCount() {
        if (this.quantityCount != 0) {
            this.quantityCount = this.quantityCount - 1
        }
    }

    val addItemEnable: Boolean
        @Bindable("productName", "productType") get() {

            val isProductNameValid = !productName.isBlank()
            val isProductTypeValid = !productType.isBlank()

            return isProductNameValid && isProductTypeValid
        }


    fun barcodeDataAdded(){
        _barcodeDataAvailable.value = Event(productBarcode)
    }


    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun onCleared() {
        super.onCleared()
    }
}
