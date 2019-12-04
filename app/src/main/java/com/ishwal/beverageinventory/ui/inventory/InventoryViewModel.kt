package com.ishwal.beverageinventory.ui.inventory

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ishwal.beverageinventory.model.InventoryItem
import com.ishwal.beverageinventory.ui.dashboard.DashboardViewModel

class InventoryViewModel : ViewModel(),
    Observable {
    companion object {
        val TAG = DashboardViewModel::class.java.simpleName
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    private val inventoryItemLiveData: MutableLiveData<InventoryItemListResponse> =
        MutableLiveData()

    val inventoryItemListHandler: LiveData<InventoryItemListResponse>
        get() = inventoryItemLiveData


    fun fetchUserInventory(auth: FirebaseAuth, databaseReferenceForItem: DatabaseReference) {
        val user = auth.currentUser
        if (user != null) {
            val query = databaseReferenceForItem.child(user.uid)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val inventoryItemList: MutableList<InventoryItem> = mutableListOf()
                    if (dataSnapshot.exists()) {
                        dataSnapshot.children.forEach {
                            val inventoryItem = it.getValue(InventoryItem::class.java)
                            if (inventoryItem != null) {
                                inventoryItem.itemId = it.key.toString()
                                inventoryItemList.add(inventoryItem)
                            }
                        }
                    }
                    inventoryItemLiveData.value =
                        InventoryItemListResponse(true, inventoryItemList, "Success")
                }

                override fun onCancelled(error: DatabaseError) {
                    inventoryItemLiveData.value =
                        InventoryItemListResponse(isSuccess = false, message = error.message)
                }

            })
        }
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }
}

data class InventoryItemListResponse(
    var isSuccess: Boolean,
    val inventoryItemList: MutableList<InventoryItem> = mutableListOf(),
    var message: String
)