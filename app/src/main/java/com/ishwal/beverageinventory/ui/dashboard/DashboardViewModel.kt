package com.ishwal.beverageinventory.ui.dashboard

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.ishwal.beverageinventory.model.InventoryItem

class DashboardViewModel : ViewModel(),
    Observable {
    companion object {
        val TAG = DashboardViewModel::class.java.simpleName
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    private val _pieChartData: MutableLiveData<HashMap<String, MutableList<InventoryItem>>> =
        MutableLiveData()


    val handlePieChartData : LiveData<HashMap<String, MutableList<InventoryItem>>>
        get() = _pieChartData

    //email
    val email: ObservableField<String> = ObservableField()

    val itemCount: ObservableField<Int> = ObservableField(0)

    fun initializeUserDetail(currentUser: FirebaseUser) {
        email.set(currentUser.email)
    }

    fun initializeItem(dataSnapshot: DataSnapshot?) {
        val itemMap: HashMap<String, MutableList<InventoryItem>> = hashMapOf()
        if(dataSnapshot != null) {
            itemCount.set(dataSnapshot.children.count())

            //preparing data for chart
            dataSnapshot.children.forEach {
                val inventoryItem = it.getValue(InventoryItem::class.java)
                if (inventoryItem != null) {
                    if (itemMap.containsKey(inventoryItem.productType)) {
                        itemMap[inventoryItem.productType]?.add(inventoryItem)
                    } else {
                        itemMap[inventoryItem.productType] = mutableListOf(inventoryItem)
                    }
                }
            }
        }
        _pieChartData.value = itemMap

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }
}
