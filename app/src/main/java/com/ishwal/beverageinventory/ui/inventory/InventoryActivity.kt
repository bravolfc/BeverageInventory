package com.ishwal.beverageinventory.ui.inventory

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.ishwal.beverageinventory.BaseActivity
import com.ishwal.beverageinventory.R
import com.ishwal.beverageinventory.databinding.ActivityInventoryBinding
import com.ishwal.beverageinventory.model.InventoryItem
import com.ishwal.beverageinventory.ui.addinventory.AddInventoryItemActivity
import kotlinx.android.synthetic.main.activity_inventory.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class InventoryActivity : BaseActivity(), InventoryAdapter.OnInventoryItemClickListener {

    var selectedItemIndex = 0

    override fun onInventoryItemClicked(inventoryItem: InventoryItem) {
       //Not implemented (Not needed)
    }

    override fun onInventoryItemLongClicked(inventoryItem: InventoryItem) {
        MaterialDialog(this).show {
            title(text = inventoryItem.productName)
            listItems(R.array.inventoryOption) { dialog, index, text ->
                when (text) {
                    "Edit" -> {
                        val intent = Intent(this@InventoryActivity, AddInventoryItemActivity::class.java)
                        intent.putExtra("ITEM", inventoryItem)
                        startActivity(intent)
                    }
                    "Delete" -> {
                        deleteItemFromDatabase(inventoryItem)
                    }
                }
            }
        }
    }

    private fun deleteItemFromDatabase(inventoryItem: InventoryItem) {
        showProgressDialog("Deleting ${inventoryItem.productName}")
        databaseReferenceForItem.child(auth.currentUser?.uid!!).child(inventoryItem.itemId!!)
            .removeValue().addOnCompleteListener {
                hideProgressDialog()
                if (it.isSuccessful) {
                    Toast.makeText(
                        this,
                        "${inventoryItem.productName} deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Error Deleting : ${it.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    companion object {
        val TAG: String = InventoryActivity::class.java.simpleName
    }

    private val inventoryViewModel: InventoryViewModel by viewModel()

    private lateinit var activityInventoryBinding: ActivityInventoryBinding


    private val auth: FirebaseAuth by inject()

    private val databaseReferenceForItem: DatabaseReference by inject(qualifier = named("ITEM"))

    private var adapter: InventoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityInventoryBinding = DataBindingUtil.setContentView(this, R.layout.activity_inventory)
        activityInventoryBinding.inventoryViewModel = this.inventoryViewModel


        inventoryViewModel.inventoryItemListHandler.observe(this, Observer {

            //Initially Sort by quantity
            it.inventoryItemList.sortBy {item ->
                item.productQuantity
            }

            if (adapter == null) {
                adapter = InventoryAdapter(this)
                inventoryRV.adapter = adapter
            }

            adapter?.setOriginalList(it.inventoryItemList)

            //Updating RecyclerView
            adapter?.submitList(it.inventoryItemList)
        })

        inventoryViewModel.fetchUserInventory(auth, databaseReferenceForItem)

        searchViewEditText.addTextChangedListener {
            if (it.isNullOrBlank()) {
                searchViewEditText.clearFocus()
                hideKeyboard(searchViewEditText)
            }
            adapter?.filter?.filter(it)
        }

        filter.setOnClickListener {
            val myItems = listOf(
                "By Quantity(Asc)",
                "By Quantity(Desc)",
                "Only Beer",
                "Only Wine",
                "Only Liquor",
                "Other"
            )

            MaterialDialog(this).show {
                title(text = "Filter Inventory")
                listItemsSingleChoice(
                    items = myItems,
                    initialSelection = selectedItemIndex
                ) { dialog, index, text ->
                    selectedItemIndex = index
                    adapter?.filterBy(text.toString())
                }
            }
        }
    }
}
