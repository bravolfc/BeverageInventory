package com.ishwal.beverageinventory.ui.addinventory

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ishwal.beverageinventory.BaseActivity
import com.ishwal.beverageinventory.R
import com.ishwal.beverageinventory.databinding.ActivityAddInventoryItemBinding
import com.ishwal.beverageinventory.model.InventoryItem
import com.ishwal.beverageinventory.utils.EventObserver
import com.ishwal.beverageinventory.utils.scanner.ScanResultModel
import com.ishwal.beverageinventory.utils.scanner.ScannerActivity
import kotlinx.android.synthetic.main.activity_add_inventory_item.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import timber.log.Timber

class AddInventoryItemActivity : BaseActivity() {

    private val addInventoryItemViewModel: AddInventoryItemViewModel by viewModel()

    private lateinit var activityAddInventoryItemBinding: ActivityAddInventoryItemBinding

    private val auth: FirebaseAuth by inject()

    private val databaseReferenceForItem : DatabaseReference by inject(qualifier = named("ITEM"))

    companion object {
        const val SCANNER_REQUEST_CODE = 1001
        val TAG: String = AddInventoryItemActivity::class.java.simpleName

        private val PRODUCT_TYPE = arrayOf("Beer", "Wine", "Liquor", "Others")
        private val VOLUME_TYPE = arrayOf("fl", "oz", "ml")
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddInventoryItemBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add_inventory_item)
        activityAddInventoryItemBinding.addInventoryItemViewModel = this.addInventoryItemViewModel

        val productTypeAdapter = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            PRODUCT_TYPE
        )
        productTypeDropDown.setAdapter(productTypeAdapter)

        val volumeUnitAdapter = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            VOLUME_TYPE
        )
        volumeUnitDropdown.setAdapter(volumeUnitAdapter)

        cancel.setOnClickListener {
            onBackPressed()
        }

        closeIcon.setOnClickListener {
            onBackPressed()
        }

        scanCodeEditText.setOnClickListener {
            val i = Intent(this, ScannerActivity::class.java)
            startActivityForResult(i, SCANNER_REQUEST_CODE)
        }

        save.setOnClickListener {
            showProgressDialog("Adding Item")
            val user = auth.currentUser
            if (user != null) {
                val inventoryItem = InventoryItem(
                    null,
                    user.uid,
                    addInventoryItemViewModel.productName,
                    addInventoryItemViewModel.productType,
                    addInventoryItemViewModel.productBarcode,
                    addInventoryItemViewModel.productVolume,
                    addInventoryItemViewModel.productVolumeUnit,
                    addInventoryItemViewModel.quantityCount
                )
                if (addInventoryItemViewModel.isNewItem) {
                    databaseReferenceForItem.child(user.uid).push().setValue(inventoryItem)
                        .addOnCompleteListener {
                            hideProgressDialog()
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    baseContext, "Item added Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackPressed()
                            } else {
                                Toast.makeText(
                                    baseContext, it.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    databaseReferenceForItem.child(user.uid)
                        .child(addInventoryItemViewModel.itemId).setValue(inventoryItem)
                        .addOnCompleteListener {
                            hideProgressDialog()
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    baseContext, "Item Updated Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackPressed()
                            } else {
                                Toast.makeText(
                                    baseContext, it.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }

        addInventoryItemViewModel.barcodeDataObserver.observe(this, EventObserver { barcode ->
            searchForBarCodeData(barcode)
        })

        Timber.d(intent.extras?.getString("CODE"))

        if(intent?.extras?.getString("CODE") != null){
            addInventoryItemViewModel.productBarcode = intent?.extras?.getString("CODE","")!!
            searchForBarCodeData(addInventoryItemViewModel.productBarcode)
        }

        if(intent?.extras?.getParcelable<InventoryItem>("ITEM") != null){
            val item = intent?.extras?.getParcelable<InventoryItem>("ITEM")

            addInventoryItemViewModel.productName = item?.productName ?: ""
            addInventoryItemViewModel.itemId = item?.itemId ?: ""
            addInventoryItemViewModel.productBarcode = item?.productBarCode ?: ""
            addInventoryItemViewModel.productType = item?.productType ?: ""
            addInventoryItemViewModel.productVolume = item?.productVolume ?: ""
            addInventoryItemViewModel.productVolumeUnit = item?.productVolumeUnit ?: ""
            addInventoryItemViewModel.quantityCount = item?.productQuantity ?: 0

            addInventoryItemViewModel.isNewItem = false
        }


    }

    private fun searchForBarCodeData(barcode: String) {
        showProgressDialog("Looking for Item for barcode")
        val user = auth.currentUser
        if (user != null) {
            val query = databaseReferenceForItem.child(user.uid).orderByChild("productBarCode")
                .equalTo(barcode).limitToFirst(1)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    hideProgressDialog()
                    Toast.makeText(
                        this@AddInventoryItemActivity,
                        databaseError.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    hideProgressDialog()
                    if (dataSnapshot.exists()) {
                        dataSnapshot.children.forEach {
                            Timber.d(it.key)
                            val inventoryItem = it.getValue(InventoryItem::class.java)
                            addInventoryItemViewModel.itemId = it.key.toString()
                            addInventoryItemViewModel.productName =
                                inventoryItem?.productName.toString()
                            addInventoryItemViewModel.productType =
                                inventoryItem?.productType.toString()
                            addInventoryItemViewModel.productVolume =
                                inventoryItem?.productVolume.toString()
                            addInventoryItemViewModel.productVolumeUnit =
                                inventoryItem?.productVolumeUnit.toString()
                            addInventoryItemViewModel.quantityCount =
                                inventoryItem?.productQuantity ?: 0
                            addInventoryItemViewModel.isNewItem = false
                        }
                    } else {
                        Toast.makeText(
                            this@AddInventoryItemActivity,
                            "No item Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val scanResult = data.getSerializableExtra("barcodeData") as ScanResultModel
                    addInventoryItemViewModel.productBarcode = scanResult.barcodeData
                }
            }
        } else {
            //Scan cancelled by user
            Timber.d("Scan cancelled by user")
        }
    }
}
